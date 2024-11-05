package pl.matsuo.interfacer.core;

import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;
import static pl.matsuo.core.util.collection.CollectionUtil.anyMatch;
import static pl.matsuo.core.util.collection.CollectionUtil.filterMap;
import static pl.matsuo.core.util.collection.CollectionUtil.flatMap;
import static pl.matsuo.core.util.collection.Pair.pair;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.utils.SourceRoot;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;
import java.util.HashSet;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import pl.matsuo.core.util.collection.Pair;
import pl.matsuo.interfacer.model.ifc.IfcResolve;
import java.util.Collections;
import pl.matsuo.interfacer.core.log.Log;

public class InterfacesAdder {

  /**
   * Modifications of source files after adding interfaces.
   */
  public record Modifications(SourceRoot source, List<Pair<IfcResolve, ClassOrInterfaceDeclaration>> modifications) {

    /** Save all changes to disk. */
    public void save() {
      if (isEmpty()) {
        source.saveAll();
      }
    }

    /** Check if there were any modifications. */
    public boolean isEmpty() {
      return modifications.isEmpty();
    }

    /** Get number of modifications. */
    public int size() {
      return modifications.size();
    }

    /**
     * Check if modifications contain interface and matched fully qualified class
     * name.
     */
    public boolean matchesAny(@NonNull String interfaceName,@NonNull String fqcn) {
      return modifications.stream().anyMatch(modification -> {
        String keyName = modification.getKey().getName();
        String valueFqcn = modification.getValue().getFullyQualifiedName().orElse(null);
        return interfaceName.equals(keyName) && fqcn.equals(valueFqcn);
      });
    }
  }

  /**
   * Add interfaces found in <code>interfacesDirectory</code> and
   * <code>compileClasspathElements
   * </code> to classes found in <code>scanDirectory</code>.
   *
   * <p>
   * Method will execute multiple passes of adding. When classes implement
   * additional interfaces,
   * it's possible that some classes may now match new interfaces.
   *
   * <pre>
   * interface SampleInterface {
   *   Integer getValue();
   * }
   *
   * interface SampleInterface2 {
   *   SampleInterface getResult();
   * }
   *
   * class SampleResult {
   *   Integer getValue();
   * }
   *
   * class Sample {
   *   SampleResult getResult();
   * }
   * </pre>
   *
   * In first pass we can add <code>SampleInterface</code> to
   * <code>SampleResult</code>. Now in
   * second pass we can add <code>SampleInterface2</code> to <code>Sample</code>.
   *
   * @param languageLevel The language level to use when parsing source files.
   *                      This is a string value
   *                      that can be the java version. It can also be string
   *                      constants like "CURRENT", "POPULAR" or
   *                      "LATEST". CURRENT is Java 18 and POPULAR is Java 11.
   *                      LATEST is the latest version of Java
   *                      that is supported by the JavaParser library.
   */
  public void addInterfacesAllFiles(
      @NonNull File scanDirectory,
      File interfacesDirectory,
      String interfacePackages,
      String languageLevel, List<String> compileClasspathElements) {

    if (interfacesDirectory == null
        && (interfacePackages == null || compileClasspathElements == null)) {
      throw new RuntimeException("""
          No interface source defined, received arguments:
              interfacesDirectory: %s
              interfacePackages: %s
              languageLevel: %s
              compileClasspathElements: %s""".formatted(
          interfacesDirectory, interfacePackages, languageLevel, compileClasspathElements));
    }

    Log.info(() -> "[InterfacesAdder] Start processing");

    try {
      List<Pair<IfcResolve, ClassOrInterfaceDeclaration>> allModifications = new ArrayList<>();
      while (true) {
        Modifications modifications = addInterfacesAllFiles(
            scanDirectory,
            interfacesDirectory,
            interfacePackages,
            languageLevel,
            compileClasspathElements,
            allModifications);

        if (!modifications.isEmpty()) {
          Log.info(() -> "[InterfacesAdder] End of processing");
          break;
        }
      }
    } catch (IOException e) {
      throw new RuntimeException("Error reading from source directory", e);
    }
  }

  /** Single pass of interface adding. */
  Modifications addInterfacesAllFiles(
      File scanDirectory,
      File interfacesDirectory,
      String interfacePackages,
      String languageLevel,
      List<String> compileClasspathElements,
      List<Pair<IfcResolve, ClassOrInterfaceDeclaration>> allModifications)
      throws IOException {

    Modifications modifications = parseAll(scanDirectory, interfacesDirectory, interfacePackages,
        languageLevel, compileClasspathElements);

    allModifications.addAll(modifications.modifications());

    // save changes on disk
    modifications.save();
    return modifications;
  }

  Modifications parseAll(File scanDirectory, File interfacesDirectory,
                         String interfacePackages, String languageLevel, List<String> compileClasspathElements) {

    // Use this more capable class loader if the maven or gradle plugin is used
    ClassLoader compileClassLoader = ClasspathInterfacesScanner.getCompileClassLoader(compileClasspathElements);

    List<File> interfacesDirectories = processInterfaces(interfacesDirectory, interfacePackages, languageLevel,
            compileClassLoader);
    ParsingContext parsingContext = new ParsingContext(compileClassLoader, scanDirectory, interfacesDirectories,
            languageLevel);

    final SourceRoot source = new SourceRoot(scanDirectory.toPath(), parsingContext.parserConfiguration);

    List<IfcResolve> ifcs = scanInterfaces(interfacesDirectories, interfacePackages, parsingContext);

    List<Pair<IfcResolve, ClassOrInterfaceDeclaration>> modifications = emptyList();
    try {
      modifications = processAllFiles(source.tryToParse(), ifcs,
              parsingContext.javaParser);
    } catch (IOException e) {
      Log.error(() -> "[InterfacesAdder] Error reading from source directory", () -> e);
    }
    return new Modifications(source, modifications);
  }

  /**
   * Process interfaces directory and interface packages arguments. This method
   * will return list of directories to scan for interfaces. If no
   * <code>interfacesDirectory</code> is provided, it will try to find interfaces
   * on classpath using the <code>interfacePackages</code> argument. The method
   * will drop quietly with a warning packages that do not exist on classpath or
   * that cannot be found in the directory structure.
   * <p>
   * If the argument <code>interfacesDirectory</code> is provided along with
   * valid <code>interfacePackages</code>, it will be scanned for all packages
   * defined in <code>interfacePackages</code> that match the directory structure.
   * <p>
   * If the argument <code>interfacesDirectory</code> is provided along with
   * empty or null <code>interfacePackages</code>, the directory will be passed
   * back.
   * <p>
   * 
   * @param interfacesDirectory      directory to scan for interfaces
   * @param interfacePackages        packages to scan for interfaces
   * @param languageLevel            language level to use when parsing source
   *                                 files
   * @param compileClassLoader      a class loader to use when scanning for interfaces
   *
   * @return list of directories to scan for interfaces
   */
  private List<File> processInterfaces(File interfacesDirectory, String interfacePackages, String languageLevel,
                                       ClassLoader compileClassLoader) {
    if (interfacesDirectory == null) {
      if (interfacePackages == null || interfacePackages.isEmpty()) {
        throw new IllegalArgumentException("""
                No interface source defined, received arguments:
                    interfacePackages: %s
                    languageLevel: %s""".formatted(
                interfacePackages, languageLevel));
      }
      String[] interfacePackagesArray = interfacePackages.split(",");
      List<File> interfaceDirectories = new ArrayList<>();
      Set<Path> rootPackagePaths = new HashSet<>();
      for (String interfacePackage : interfacePackagesArray) {
        String packagePath = interfacePackage.replace('.', '/');
        URL packageURL = compileClassLoader.getResource(packagePath);
        if (packageURL == null) {
          Log.warn(() -> "[InterfacesAdder] Package: %s not found in classpath, ignoring!".formatted(interfacePackage));
        } else {
          File packageDirectory = new File(packageURL.getPath());
          if (packageDirectory.exists() && packageDirectory.isDirectory()) {
            Path packageFileRoot = packageDirectory.toPath().toAbsolutePath().getParent();
            for (int i = 1; i < packagePath.split("/").length; i++) {
              packageFileRoot = packageFileRoot.getParent();
            }
            if (rootPackagePaths.add(packageFileRoot)) {
              interfaceDirectories.add(packageDirectory);
            }
          }
        }
      }
      return interfaceDirectories;
    } else {
        if (interfacePackages != null && !interfacePackages.isEmpty()) {
            String[] interfacePackagesArray = interfacePackages.split(",");
            for (String interfacePackage : interfacePackagesArray) {
                String packagePath = interfacePackage.replace('.', '/');
                File packageDirectory = new File(interfacesDirectory, packagePath);
                if (!packageDirectory.exists() || !packageDirectory.isDirectory()) {
                    Log.warn(() -> "[InterfacesAdder] Package: %s not found in the given interfaces directory: %s, ignoring!".formatted(
                            interfacePackage,
                            interfacesDirectory));
                }
            }
        }
        return Collections.singletonList(interfacesDirectory);
    }
  }

  /** Parse file using internal {@link JavaParser}. */
  public ParseResult<CompilationUnit> parseFile(JavaParser javaParser, File file) {
    try {
      return javaParser.parse(file);
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  /** Search for interfaces on classpath and in source folder. */
  public List<IfcResolve> scanInterfaces(List<File> interfacesDirectories, String interfacePackages,
      ParsingContext parsingContext) {
    Set<IfcResolve> uniqueResolvers = new HashSet<>();
    uniqueResolvers.addAll(
        new ClasspathInterfacesScanner()
            .scanInterfacesFromClasspath(
                parsingContext.classLoader, interfacePackages, parsingContext.typeSolver));
    uniqueResolvers.addAll(
        new SourceInterfacesScanner()
            .scanInterfacesFromSrc(parsingContext.parserConfiguration, interfacesDirectories));
    List<IfcResolve> ifcs = new ArrayList<>(uniqueResolvers);
    Comparator<IfcResolve> comparator = comparing(i -> -i.getMethods().size());
    ifcs.sort(comparator);
    return ifcs;
  }

  /**
   * Go through all parsed generated classes and try adding interfaces to them.
   */
  List<Pair<IfcResolve, ClassOrInterfaceDeclaration>> processAllFiles(
      List<ParseResult<CompilationUnit>> parseResults,
      List<IfcResolve> ifcs,
      JavaParser javaParser) {

    return flatMap(
        parseResults,
        parseResult -> {
          // Only deal with files without parse errors
          if (parseResult.isSuccessful()) {
            return parseResult
                .getResult()
                .map(
                    cu -> {
                      // Do the actual logic
                      return addInterfaces(cu, ifcs, javaParser);
                    })
                .orElse(emptyList());
          } else {
            Log.warn(() -> "[InterfacesAdder] Parse failure for " + parseResult.getProblems());
            return emptyList();
          }
        });
  }

  /** Add interfaces to class parsed into <code>compilationUnit</code>. */
  private List<Pair<IfcResolve, ClassOrInterfaceDeclaration>> addInterfaces(
      CompilationUnit compilationUnit, List<IfcResolve> ifcs, JavaParser javaParser) {
    return compilationUnit
        .getPrimaryType()
        .map(
            primaryType -> primaryType.isClassOrInterfaceDeclaration()
                ? (ClassOrInterfaceDeclaration) primaryType
                : null)
        .filter(declaration -> !declaration.isInterface())
        .map(
            declaration -> filterMap(
                ifcs, ifc -> processDeclarationWithInterface(declaration, ifc, javaParser)))
        .orElse(emptyList());
  }

  /**
   * Check if class <code>declaration</code> is matching interface
   * <code>ifc</code>. If true, add
   * interface to the class. Return <code>pair</code> representing interface and
   * class. Return
   * <code>null</code> if interface was not added.
   */
  private Pair<IfcResolve, ClassOrInterfaceDeclaration> processDeclarationWithInterface(
      ClassOrInterfaceDeclaration declaration, IfcResolve ifc, JavaParser javaParser) {

    Map<String, String> resolvedTypeVariables = ifc.matches(declaration);

    // if any of the declaration's ancestors is already assignable to ifc
    boolean canBeAssignedTo = canBeAssignedTo(declaration, ifc);
    if (resolvedTypeVariables != null && !canBeAssignedTo) {
      return addInterfaceToClassDeclaration(declaration, ifc, javaParser, resolvedTypeVariables);
    }

    return null;
  }

  /**
   * Create interface <code>ifc</code> representation in <code>javaparser</code>
   * and add it to the
   * class <code>declaration</code>.
   */
  private Pair<IfcResolve, ClassOrInterfaceDeclaration> addInterfaceToClassDeclaration(
      ClassOrInterfaceDeclaration declaration,
      IfcResolve ifc,
      JavaParser javaParser,
      Map<String, String> resolvedTypeVariables) {
    Log.info(() -> "[InterfacesAdder] Modifying the class: %s with interface: %s".formatted(declaration.getFullyQualifiedName().orElse(""), ifc.getName()));

    ClassOrInterfaceType type = // new ClassOrInterfaceType(ifc.getName());
        javaParser
            .parseClassOrInterfaceType(ifc.getGenericName(resolvedTypeVariables))
            .getResult()
            .orElseThrow(() -> new RuntimeException(""));
    // type.setTypeArguments(ifc.getTypeArguments(resolvedTypeVariables));

    declaration.addImplementedType(type);
    return pair(ifc, declaration);
  }

  /**
   * Check if <code>declaration</code> is representing subtype of interface
   * <code>ifc</code>.
   */
  private boolean canBeAssignedTo(ClassOrInterfaceDeclaration declaration, IfcResolve ifc) {
    return anyMatch(
        declaration.resolve().getAncestors(),
        ancestor -> {
          try {
            return ifc.getResolvedTypeDeclaration().isAssignableBy(ancestor);
          } catch (RuntimeException e) {
            // e.printStackTrace();
            return false;
          }
        });
  }
}
