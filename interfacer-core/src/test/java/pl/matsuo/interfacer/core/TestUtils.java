package pl.matsuo.interfacer.core;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import lombok.extern.slf4j.Slf4j;
import pl.matsuo.core.util.collection.Pair;
import pl.matsuo.interfacer.model.ifc.IfcResolve;

@Slf4j
public class TestUtils {

  public static InterfacesAdder.Modifications doTestInterfaceMatching(File classResourceDir, Class<?> ifc,
      String javaVersion) {
    InterfacesAdder interfacesAdder = new InterfacesAdder();

    return interfacesAdder.parseAll(classResourceDir, null, ifc.getPackageName(), javaVersion, emptyList());
  }

  public static InterfacesAdder.Modifications doTestInterfaceMatching(File classResourceDir, String interfacePackage,
      String javaVersion) {
    InterfacesAdder interfacesAdder = new InterfacesAdder();
    return interfacesAdder.parseAll(classResourceDir, null, interfacePackage, javaVersion, emptyList());
  }

  public static InterfacesAdder.Modifications doTestInterfaceMatching(File classResourceDir, File interfaceDirectory,
      String javaVersion) {
    InterfacesAdder interfacesAdder = new InterfacesAdder();
    return interfacesAdder.parseAll(classResourceDir, interfaceDirectory, "", javaVersion, emptyList());
  }

  public static List<Pair<IfcResolve, ClassOrInterfaceDeclaration>> doTestInterface(String className, Class<?> ifc) {
    return doTestInterface(className, ifc, "21");
  }

  public static List<Pair<IfcResolve, ClassOrInterfaceDeclaration>> doTestInterface(String className, Class<?> ifc,
      String javaVersion) {
    InterfacesAdder interfacesAdder = new InterfacesAdder();

    File sampleClassFile = TestUtils.fileForResource(className);
    File scanDir = sampleClassFile.getParentFile();
    ClassLoader loader = ClasspathInterfacesScanner.getCompileClassLoader(emptyList());
    ParsingContext parsingContext = new ParsingContext(loader, scanDir, Collections.singletonList(scanDir),
        javaVersion);

    ClasspathInterfacesScanner interfacesScanner = new ClasspathInterfacesScanner();
    IfcResolve genericInterface = interfacesScanner.processClassFromClasspath(ifc, parsingContext.typeSolver);

    assertNotNull(genericInterface);

    ParseResult<CompilationUnit> compilationUnitParseResult = interfacesAdder.parseFile(parsingContext.javaParser,
        sampleClassFile);

    if (!compilationUnitParseResult.isSuccessful()) {
      log.info("" + compilationUnitParseResult.getProblems());
    }

    List<Pair<IfcResolve, ClassOrInterfaceDeclaration>> modifications = interfacesAdder
        .processAllFiles(List.of(compilationUnitParseResult), List.of(genericInterface), parsingContext.javaParser);

    modifications.forEach(mod -> log.info(mod.toString()));

    return modifications;
  }

  public static List<Pair<IfcResolve, ClassOrInterfaceDeclaration>> doTestInterface(String className,
      Class<?>... ifcs) {
    InterfacesAdder interfacesAdder = new InterfacesAdder();

    File sampleClassFile = TestUtils.fileForResource(className);
    File scanDir = sampleClassFile.getParentFile();
    ClassLoader loader = ClasspathInterfacesScanner.getCompileClassLoader(emptyList());
    ParsingContext parsingContext = new ParsingContext(loader, scanDir, Collections.singletonList(scanDir), "21");

    ClasspathInterfacesScanner interfacesScanner = new ClasspathInterfacesScanner();
    List<IfcResolve> ifcResolves = new ArrayList<>();
    for (Class<?> ifc : ifcs) {
      IfcResolve ifcResolve = interfacesScanner.processClassFromClasspath(ifc, parsingContext.typeSolver);
      if (ifcResolve != null) {
        ifcResolves.add(ifcResolve);
      }
    }
    assertNotNull(ifcResolves);
    assertFalse(ifcResolves.isEmpty());

    ParseResult<CompilationUnit> compilationUnitParseResult = interfacesAdder.parseFile(parsingContext.javaParser,
        sampleClassFile);

    if (!compilationUnitParseResult.isSuccessful()) {
      log.info("" + compilationUnitParseResult.getProblems());
    }

    List<Pair<IfcResolve, ClassOrInterfaceDeclaration>> modifications = interfacesAdder
        .processAllFiles(List.of(compilationUnitParseResult), ifcResolves, parsingContext.javaParser);

    modifications.forEach(mod -> log.info(mod.toString()));

    return modifications;
  }

  public static List<Pair<IfcResolve, ClassOrInterfaceDeclaration>> doTestInterface(String className,
      File interfacesDir) {
    return TestUtils.doTestInterface(className, interfacesDir, "POPULAR");
  }

  public static List<Pair<IfcResolve, ClassOrInterfaceDeclaration>> doTestInterface(String className,
      File interfacesDir, String javaVersion) {

    InterfacesAdder interfacesAdder = new InterfacesAdder();

    File sampleClassFile = TestUtils.fileForResource(className);
    File scanDir = sampleClassFile.getParentFile();
    ClassLoader loader = ClasspathInterfacesScanner.getCompileClassLoader(emptyList());
    ParsingContext parsingContext = new ParsingContext(loader, scanDir, Collections.singletonList(interfacesDir),
        javaVersion);

    SourceInterfacesScanner interfacesScanner = new SourceInterfacesScanner();

    List<IfcResolve> ifcResolves = interfacesScanner.scanInterfacesFromSrc(parsingContext.parserConfiguration,
        Collections.singletonList(interfacesDir));
    assertFalse(ifcResolves.isEmpty());

    ParseResult<CompilationUnit> compilationUnitParseResult = interfacesAdder.parseFile(parsingContext.javaParser,
        sampleClassFile);

    if (!compilationUnitParseResult.isSuccessful()) {
      log.info("" + compilationUnitParseResult.getProblems());
    }

    List<Pair<IfcResolve, ClassOrInterfaceDeclaration>> modifications = interfacesAdder
        .processAllFiles(List.of(compilationUnitParseResult), ifcResolves, parsingContext.javaParser);

    modifications.forEach(mod -> log.info(mod.toString()));

    return modifications;
  }

  private static File fileForResource(String resourcePath) {
    return new File(Objects.requireNonNull(TestUtils.class.getResource(resourcePath)).getFile());
  }

}
