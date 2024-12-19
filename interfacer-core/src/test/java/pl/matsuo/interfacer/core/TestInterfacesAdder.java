package pl.matsuo.interfacer.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import java.io.File;
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
    List<Pair<IfcResolve, ClassOrInterfaceDeclaration>> modifications = TestUtils
        .doTestInterface("/classes/test/SampleClass.java", HasName.class);
    assertEquals(1, modifications.size());
    assertEquals(1, modifications.getFirst().getValue().getImplementedTypes().size());
    assertEquals("HasName", modifications.getFirst().getValue().getImplementedTypes().getFirst().getNameAsString());
  }

  @Test
  public void testJava21FeaturesSupported() {
    List<Pair<IfcResolve, ClassOrInterfaceDeclaration>> modifications = TestUtils
        .doTestInterface("/classes/test/Sample21Class.java", HasName.class, "21");
    assertEquals(1, modifications.size());
    assertEquals(1, modifications.getFirst().getValue().getImplementedTypes().size());
    assertEquals("HasName", modifications.getFirst().getValue().getImplementedTypes().getFirst().getNameAsString());
  }

  @Test
  public void testJava18FeaturesSupported() {
    List<Pair<IfcResolve, ClassOrInterfaceDeclaration>> modifications = TestUtils
        .doTestInterface("/classes/test/Sample18Class.java", HasName.class, "18");
    assertEquals(1, modifications.size());
    assertEquals(1, modifications.getFirst().getValue().getImplementedTypes().size());
    assertEquals("HasName", modifications.getFirst().getValue().getImplementedTypes().getFirst().getNameAsString());
  }

  @Test
  public void testJavaCurrentFeaturesSupported() {
    List<Pair<IfcResolve, ClassOrInterfaceDeclaration>> modifications = TestUtils
        .doTestInterface("/classes/test/Sample18Class.java", HasName.class, "CURRENT");
    assertEquals(1, modifications.size());
    assertEquals(1, modifications.getFirst().getValue().getImplementedTypes().size());
    assertEquals("HasName", modifications.getFirst().getValue().getImplementedTypes().getFirst().getNameAsString());
  }

  @Test
  public void testJava17FeaturesSupported() {
    List<Pair<IfcResolve, ClassOrInterfaceDeclaration>> modifications = TestUtils
        .doTestInterface("/classes/test/Sample17Class.java", HasName.class, "17");
    assertEquals(1, modifications.size());
    assertEquals(1, modifications.getFirst().getValue().getImplementedTypes().size());
    assertEquals("HasName", modifications.getFirst().getValue().getImplementedTypes().getFirst().getNameAsString());
  }

  @Test
  public void testJava16FeaturesSupported() {
    List<Pair<IfcResolve, ClassOrInterfaceDeclaration>> modifications = TestUtils
        .doTestInterface("/classes/test/Sample16Class.java", HasName.class, "16");
    assertEquals(1, modifications.size());
    assertEquals(1, modifications.getFirst().getValue().getImplementedTypes().size());
    assertEquals("HasName", modifications.getFirst().getValue().getImplementedTypes().getFirst().getNameAsString());
  }

  @Test
  public void testJava11FeaturesSupported() {
    List<Pair<IfcResolve, ClassOrInterfaceDeclaration>> modifications = TestUtils
        .doTestInterface("/classes/test/Sample11Class.java", HasName.class, "11");
    assertEquals(1, modifications.size());
    assertEquals(1, modifications.getFirst().getValue().getImplementedTypes().size());
    assertEquals("HasName", modifications.getFirst().getValue().getImplementedTypes().getFirst().getNameAsString());
  }

  @Test
  public void testJavaPopularFeaturesSupported() {
    List<Pair<IfcResolve, ClassOrInterfaceDeclaration>> modifications = TestUtils
        .doTestInterface("/classes/test/Sample11Class.java", HasName.class, "POPULAR");
    assertEquals(1, modifications.size());
    assertEquals(1, modifications.getFirst().getValue().getImplementedTypes().size());
    assertEquals("HasName", modifications.getFirst().getValue().getImplementedTypes().getFirst().getNameAsString());
  }

  @Test
  public void testAdditionalMatchedInterfaceApplied() {
    List<Pair<IfcResolve, ClassOrInterfaceDeclaration>> modifications = TestUtils
        .doTestInterface("/classes/test/SampleClass.java", HasName.class, SampleInterface.class);
    assertEquals(2, modifications.size());
    assertEquals(2, modifications.getFirst().getValue().getImplementedTypes().size());
    assertEquals("HasName", modifications.getFirst().getValue().getImplementedTypes().getFirst().getNameAsString());
    assertEquals("SampleInterface",
        modifications.getFirst().getValue().getImplementedTypes().getLast().getNameAsString());
  }

  @Test
  public void testGenericInterface() {
    List<Pair<IfcResolve, ClassOrInterfaceDeclaration>> modifications = TestUtils
        .doTestInterface("/classes/test/SampleGenericClass.java", GenericInterface.class);
    assertEquals(1, modifications.size());
    assertEquals(1, modifications.getFirst().getValue().getImplementedTypes().size());
    assertEquals("GenericInterface",
        modifications.getFirst().getValue().getImplementedTypes().getFirst().getNameAsString());
  }

  @Test
  public void testNotGenericInterface() {
    List<Pair<IfcResolve, ClassOrInterfaceDeclaration>> modifications = TestUtils
        .doTestInterface("/classes/test/SampleNotGenericClass.java", GenericInterface.class);
    assertEquals(0, modifications.size());
  }

  @Test
  public void testMutableOwner() {
    List<Pair<IfcResolve, ClassOrInterfaceDeclaration>> modifications = TestUtils
        .doTestInterface("/classes/test/SampleMutableClass.java", MutableOwner.class);
    assertEquals(1, modifications.size());
    assertEquals(1, modifications.getFirst().getValue().getImplementedTypes().size());
    assertEquals("MutableOwner",
        modifications.getFirst().getValue().getImplementedTypes().getFirst().getNameAsString());
  }

  @Test
  public void testAllTargetClassesMatchingGivenResourceDir() {
    File resourceDir = new File("src/test/resources/classes/test/");
    InterfacesAdder.Modifications modifications = TestUtils.doTestInterfaceMatching(resourceDir, HasName.class, "21");
    assertFalse(modifications.isEmpty());
    assertEquals(9, modifications.size());
    assertTrue(modifications.matchesAny(MutableOwner.class.getCanonicalName(), "test.SampleMutableClass"));
    assertTrue(modifications.matchesAny(GenericInterface.class.getCanonicalName(), "test.SampleGenericClass"));
    assertTrue(modifications.matchesAny(HasName.class.getCanonicalName(), "test.Sample11Class"));
    assertTrue(modifications.matchesAny(HasName.class.getCanonicalName(), "test.Sample16Class"));
    assertTrue(modifications.matchesAny(HasName.class.getCanonicalName(), "test.Sample17Class"));
    assertTrue(modifications.matchesAny(HasName.class.getCanonicalName(), "test.Sample18Class"));
    assertTrue(modifications.matchesAny(HasName.class.getCanonicalName(), "test.Sample21Class"));
    assertTrue(modifications.matchesAny(HasName.class.getCanonicalName(), "test.SampleClass"));
    assertTrue(modifications.matchesAny(SampleInterface.class.getCanonicalName(), "test.SampleClass"));
  }

  @Test
  public void testAllTargetClassesMatchingGivenInterfacePackage() {
    File resourceDir = new File("src/test/resources/classes/test/");
    InterfacesAdder.Modifications modifications = TestUtils.doTestInterfaceMatching(resourceDir,
        "pl.matsuo.interfacer.showcase", "21");
    assertFalse(modifications.isEmpty());
    assertEquals(9, modifications.size());
    assertTrue(modifications.matchesAny(MutableOwner.class.getCanonicalName(), "test.SampleMutableClass"));
    assertTrue(modifications.matchesAny(GenericInterface.class.getCanonicalName(), "test.SampleGenericClass"));
    assertTrue(modifications.matchesAny(HasName.class.getCanonicalName(), "test.Sample11Class"));
    assertTrue(modifications.matchesAny(HasName.class.getCanonicalName(), "test.Sample16Class"));
    assertTrue(modifications.matchesAny(HasName.class.getCanonicalName(), "test.Sample17Class"));
    assertTrue(modifications.matchesAny(HasName.class.getCanonicalName(), "test.Sample18Class"));
    assertTrue(modifications.matchesAny(HasName.class.getCanonicalName(), "test.Sample21Class"));
    assertTrue(modifications.matchesAny(HasName.class.getCanonicalName(), "test.SampleClass"));
    assertTrue(modifications.matchesAny(SampleInterface.class.getCanonicalName(), "test.SampleClass"));
  }

}
