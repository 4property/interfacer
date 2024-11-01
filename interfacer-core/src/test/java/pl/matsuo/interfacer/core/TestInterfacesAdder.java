package pl.matsuo.interfacer.core;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import pl.matsuo.core.util.collection.Pair;
import pl.matsuo.interfacer.model.ifc.IfcResolve;
import pl.matsuo.interfacer.showcase.GenericInterface;
import pl.matsuo.interfacer.showcase.HasName;
import pl.matsuo.interfacer.showcase.MutableOwner;
import pl.matsuo.interfacer.showcase.SampleInterface;

@Slf4j
public class TestInterfacesAdder {

  @Test
  public void testHasNameApplied() {
    List<Pair<IfcResolve, ClassOrInterfaceDeclaration>> modifications = doTestInterface(
        "/classes/test/SampleClass.java", HasName.class);
    assertEquals(1, modifications.size());
    assertEquals(1, modifications.get(0).getValue().getImplementedTypes().size());
    assertEquals("HasName", modifications.get(0).getValue().getImplementedTypes().get(0).getNameAsString());
  }

  @Test
  public void testJava21FeaturesSupported() {
    List<Pair<IfcResolve, ClassOrInterfaceDeclaration>> modifications = doTestInterface(
        "/classes/test/Sample21Class.java", HasName.class, "21");
    assertEquals(1, modifications.size());
    assertEquals(1, modifications.get(0).getValue().getImplementedTypes().size());
    assertEquals("HasName", modifications.get(0).getValue().getImplementedTypes().get(0).getNameAsString());
  }

  @Test
  public void testJava18FeaturesSupported() {
    List<Pair<IfcResolve, ClassOrInterfaceDeclaration>> modifications = doTestInterface(
        "/classes/test/Sample18Class.java", HasName.class, "18");
    assertEquals(1, modifications.size());
    assertEquals(1, modifications.get(0).getValue().getImplementedTypes().size());
    assertEquals("HasName", modifications.get(0).getValue().getImplementedTypes().get(0).getNameAsString());
  }

  @Test
  public void testJavaCurrentFeaturesSupported() {
    List<Pair<IfcResolve, ClassOrInterfaceDeclaration>> modifications = doTestInterface(
        "/classes/test/Sample18Class.java", HasName.class, "CURRENT");
    assertEquals(1, modifications.size());
    assertEquals(1, modifications.get(0).getValue().getImplementedTypes().size());
    assertEquals("HasName", modifications.get(0).getValue().getImplementedTypes().get(0).getNameAsString());
  }


  @Test
  public void testJava17FeaturesSupported() {
    List<Pair<IfcResolve, ClassOrInterfaceDeclaration>> modifications = doTestInterface(
        "/classes/test/Sample17Class.java", HasName.class, "17");
    assertEquals(1, modifications.size());
    assertEquals(1, modifications.get(0).getValue().getImplementedTypes().size());
    assertEquals("HasName", modifications.get(0).getValue().getImplementedTypes().get(0).getNameAsString());
  }

  @Test
  public void testJava16FeaturesSupported() {
    List<Pair<IfcResolve, ClassOrInterfaceDeclaration>> modifications = doTestInterface(
        "/classes/test/Sample16Class.java", HasName.class, "16");
    assertEquals(1, modifications.size());
    assertEquals(1, modifications.get(0).getValue().getImplementedTypes().size());
    assertEquals("HasName", modifications.get(0).getValue().getImplementedTypes().get(0).getNameAsString());
  }

  @Test
  public void testJava11FeaturesSupported() {
    List<Pair<IfcResolve, ClassOrInterfaceDeclaration>> modifications = doTestInterface(
        "/classes/test/Sample11Class.java", HasName.class, "11");
    assertEquals(1, modifications.size());
    assertEquals(1, modifications.get(0).getValue().getImplementedTypes().size());
    assertEquals("HasName", modifications.get(0).getValue().getImplementedTypes().get(0).getNameAsString());
  }

@Test
  public void testJavaPopularFeaturesSupported() {
    List<Pair<IfcResolve, ClassOrInterfaceDeclaration>> modifications = doTestInterface(
        "/classes/test/Sample11Class.java", HasName.class, "POPULAR");
    assertEquals(1, modifications.size());
    assertEquals(1, modifications.get(0).getValue().getImplementedTypes().size());
    assertEquals("HasName", modifications.get(0).getValue().getImplementedTypes().get(0).getNameAsString());
  }

  @Test
  public void testAdditionalMatchedInterfaceApplied() {
    List<Pair<IfcResolve, ClassOrInterfaceDeclaration>> modifications = doTestInterface(
        "/classes/test/SampleClass.java", HasName.class, SampleInterface.class);
    assertEquals(2, modifications.size());
    assertEquals(2, modifications.get(0).getValue().getImplementedTypes().size());
    assertEquals("HasName", modifications.get(0).getValue().getImplementedTypes().get(0).getNameAsString());
    assertEquals("SampleInterface", modifications.get(0).getValue().getImplementedTypes().get(1).getNameAsString());
  }

  @Test
  public void testGenericInterface() {
    List<Pair<IfcResolve, ClassOrInterfaceDeclaration>> modifications = doTestInterface(
        "/classes/test/SampleGenericClass.java", GenericInterface.class);
    assertEquals(1, modifications.size());
    assertEquals(1, modifications.get(0).getValue().getImplementedTypes().size());
    assertEquals("GenericInterface", modifications.get(0).getValue().getImplementedTypes().get(0).getNameAsString());
  }

  @Test
  public void testNotGenericInterface() {
    List<Pair<IfcResolve, ClassOrInterfaceDeclaration>> modifications = doTestInterface(
        "/classes/test/SampleNotGenericClass.java", GenericInterface.class);
    assertEquals(0, modifications.size());
  }

  @Test
  public void testMutableOwner() {
    List<Pair<IfcResolve, ClassOrInterfaceDeclaration>> modifications = doTestInterface(
        "/classes/test/SampleMutableClass.java", MutableOwner.class);
    assertEquals(1, modifications.size());
    assertEquals(1, modifications.get(0).getValue().getImplementedTypes().size());
    assertEquals("MutableOwner", modifications.get(0).getValue().getImplementedTypes().get(0).getNameAsString());
  }

  public static File fileForResource(String resourcePath) {
    return new File(TestInterfacesAdder.class.getResource(resourcePath).getFile());
  }

  public List<Pair<IfcResolve, ClassOrInterfaceDeclaration>> doTestInterface(
      String className, Class<?> ifc) {
    return doTestInterface(className, ifc, "21");
  }

  public List<Pair<IfcResolve, ClassOrInterfaceDeclaration>> doTestInterface(String className,
      Class<?> ifc, String javaVersion) {
    InterfacesAdder interfacesAdder = new InterfacesAdder();

    File sampleClassFile = fileForResource(className);
    File scanDir = sampleClassFile.getParentFile();

    ParsingContext parsingContext = new ParsingContext(emptyList(), scanDir, scanDir, javaVersion);

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

  public List<Pair<IfcResolve, ClassOrInterfaceDeclaration>> doTestInterface(String className,
      Class<?>... ifcs) {
    InterfacesAdder interfacesAdder = new InterfacesAdder();

    File sampleClassFile = fileForResource(className);
    File scanDir = sampleClassFile.getParentFile();

    ParsingContext parsingContext = new ParsingContext(emptyList(), scanDir, scanDir, "21");

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

  public List<Pair<IfcResolve, ClassOrInterfaceDeclaration>> doTestInterface(
      String className, File interfacesDir) {
    return doTestInterface(className, interfacesDir, "POPULAR");
  }

  public List<Pair<IfcResolve, ClassOrInterfaceDeclaration>> doTestInterface(String className,
      File interfacesDir, String javaVersion) {
    SourceInterfacesScanner interfacesScanner = new SourceInterfacesScanner();

    List<IfcResolve> ifcResolves = interfacesScanner.scanInterfacesFromSrc(null, interfacesDir);
    assertFalse(ifcResolves.isEmpty());

    InterfacesAdder interfacesAdder = new InterfacesAdder();

    File sampleClassFile = fileForResource(className);
    File scanDir = sampleClassFile.getParentFile();

    ParsingContext parsingContext = new ParsingContext(emptyList(), scanDir, interfacesDir, javaVersion);

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
}
