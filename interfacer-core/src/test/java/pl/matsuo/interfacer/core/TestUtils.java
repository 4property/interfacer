package pl.matsuo.interfacer.core;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import lombok.extern.slf4j.Slf4j;
import pl.matsuo.core.util.collection.Pair;
import pl.matsuo.interfacer.model.ifc.IfcResolve;

@Slf4j
public class TestUtils {

  public static InterfacesAdder.Modifications doTestInterfaceMatching(String classResourcePath, Class<?> ifc,
      String javaVersion) {
    InterfacesAdder interfacesAdder = new InterfacesAdder();
    File scanDir = TestUtils.fileForResource(classResourcePath);

    return interfacesAdder.parseAll(scanDir, null, ifc.getPackageName(), javaVersion, emptyList());
  }

  public static InterfacesAdder.Modifications doTestInterfaceMatching(String classResourcePath, String interfacePackage,
      String javaVersion) {
    InterfacesAdder interfacesAdder = new InterfacesAdder();
    File scanDir = TestUtils.fileForResource(classResourcePath);

    return interfacesAdder.parseAll(scanDir, null, interfacePackage, javaVersion, emptyList());
  }

  public static InterfacesAdder.Modifications doTestInterfaceMatching(String classResourcePath, File interfaceDirectory,
      String javaVersion) {
    InterfacesAdder interfacesAdder = new InterfacesAdder();
    File scanDir = TestUtils.fileForResource(classResourcePath);

    return interfacesAdder.parseAll(scanDir, interfaceDirectory, "", javaVersion, emptyList());
  }

  public static List<Pair<IfcResolve, ClassOrInterfaceDeclaration>> doTestInterface(
      String className, Class<?> ifc) {
    return doTestInterface(className, ifc, "21");
  }

  public static List<Pair<IfcResolve, ClassOrInterfaceDeclaration>> doTestInterface(String className,
      Class<?> ifc, String javaVersion) {
    InterfacesAdder interfacesAdder = new InterfacesAdder();

    File sampleClassFile = TestUtils.fileForResource(className);
    File scanDir = sampleClassFile.getParentFile();

    ParsingContext parsingContext = new ParsingContext(emptyList(), scanDir, Collections.singletonList(scanDir),
        javaVersion);

    ClasspathInterfacesScanner interfacesScanner = new ClasspathInterfacesScanner();
    IfcResolve genericInterface = interfacesScanner.processClassFromClasspath(ifc, parsingContext.typeSolver);

    assertNotNull(genericInterface);

    ParseResult<CompilationUnit> compilationUnitParseResult = interfacesAdder.parseFile(parsingContext.javaParser,
        sampleClassFile);

    if (!compilationUnitParseResult.isSuccessful()) {
      log.info("" + compilationUnitParseResult.getProblems());
    }

    List<Pair<IfcResolve, ClassOrInterfaceDeclaration>> modifications = interfacesAdder.processAllFiles(
        asList(compilationUnitParseResult),
        asList(genericInterface),
        parsingContext.javaParser);

    modifications.forEach(mod -> log.info(mod.toString()));

    return modifications;
  }

  public static List<Pair<IfcResolve, ClassOrInterfaceDeclaration>> doTestInterface(String className,
      Class<?>... ifcs) {
    InterfacesAdder interfacesAdder = new InterfacesAdder();

    File sampleClassFile = TestUtils.fileForResource(className);
    File scanDir = sampleClassFile.getParentFile();

    ParsingContext parsingContext = new ParsingContext(emptyList(), scanDir, Collections.singletonList(scanDir), "21");

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

    List<Pair<IfcResolve, ClassOrInterfaceDeclaration>> modifications = interfacesAdder.processAllFiles(
        asList(compilationUnitParseResult),
        ifcResolves,
        parsingContext.javaParser);

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

    ParsingContext parsingContext = new ParsingContext(emptyList(), scanDir, Collections.singletonList(interfacesDir),
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

    List<Pair<IfcResolve, ClassOrInterfaceDeclaration>> modifications = interfacesAdder.processAllFiles(
        asList(compilationUnitParseResult), ifcResolves,
        parsingContext.javaParser);

    modifications.forEach(mod -> log.info(mod.toString()));

    return modifications;
  }

  private static File fileForResource(String resourcePath) {
    return new File(TestUtils.class.getResource(resourcePath).getFile());
  }

}
