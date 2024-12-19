import java.nio.file.Files
import java.nio.file.Paths

// Check if the build was successful
def log = new File(basedir, 'build.log')
assert log.exists()
assert log.text.contains('BUILD SUCCESS')

// Path to the generated BasicKeyValue class
def noInterfacesObjectPath = Paths.get(basedir.toString(), 'target', 'generated-sources', 'avro', 'pl', 'matsuo', 'interfacer', 'avro', 'NoInterfacesObject.java')
// Check if the generated BasicKeyValue class exists
assert Files.exists(noInterfacesObjectPath)

def namedRecordPath = Paths.get(basedir.toString(), 'target', 'generated-sources', 'avro', 'pl', 'matsuo', 'interfacer', 'avro', 'NamedRecord.java')
assert Files.exists(namedRecordPath)

def genericStringPath = Paths.get(basedir.toString(), 'target', 'generated-sources', 'avro', 'pl', 'matsuo', 'interfacer', 'avro', 'GenericString.java')
assert Files.exists(genericStringPath)

def genericShapePath = Paths.get(basedir.toString(), 'target', 'generated-sources', 'avro', 'pl', 'matsuo', 'interfacer', 'avro', 'GenericShape.java')
assert Files.exists(genericShapePath)

def genericDatePath = Paths.get(basedir.toString(), 'target', 'generated-sources', 'avro', 'pl', 'matsuo', 'interfacer', 'avro', 'GenericDate.java')
assert Files.exists(genericDatePath)

def addressPath = Paths.get(basedir.toString(), 'target', 'generated-sources', 'avro', 'pl', 'matsuo', 'interfacer', 'avro', 'Address.java')
assert Files.exists(addressPath)

def personPath = Paths.get(basedir.toString(), 'target', 'generated-sources', 'avro', 'pl', 'matsuo', 'interfacer', 'avro', 'Person.java')
assert Files.exists(personPath)

println "All generated files exist as expected."

// Read the content of the generated BasicKeyValue class and check if it implements IKeyValue
def noInterfacesObjectContent = new String(Files.readAllBytes(noInterfacesObjectPath))    
assert noInterfacesObjectContent.contains('implements org.apache.avro.specific.SpecificRecord')
println "NoInterfacesObject class correctly implements SpecificRecord interface. But it does not match any interfacer matched interfaces as expected."

def namedRecordContent = new String(Files.readAllBytes(namedRecordPath))    
assert namedRecordContent.contains('implements org.apache.avro.specific.SpecificRecord, pl.matsuo.interfacer.showcase.MutableOwner, com.example.MutableOwner2, pl.matsuo.interfacer.showcase.HasName')
println "NamedRecord class correctly implements HasName, MutableOwner & MutableOwner2 interfaces as expected."

def genericStringContent = new String(Files.readAllBytes(genericStringPath))    
assert genericStringContent.contains('implements org.apache.avro.specific.SpecificRecord, pl.matsuo.interfacer.showcase.GenericInterface<java.lang.CharSequence>')
println "GenericString class correctly implements GenericInterface<CharSequence> interface as expected."

def genericShapeContent = new String(Files.readAllBytes(genericShapePath))    
assert genericShapeContent.contains('implements org.apache.avro.specific.SpecificRecord, pl.matsuo.interfacer.showcase.GenericInterface<pl.matsuo.interfacer.avro.Shape>')
println "GenericShape class correctly implements GenericInterface<Shape> interface as expected."

def genericDateContent = new String(Files.readAllBytes(genericDatePath))    
assert genericDateContent.contains('implements org.apache.avro.specific.SpecificRecord, pl.matsuo.interfacer.showcase.GenericInterface<java.time.LocalDate>')
println "GenericDate class correctly implements GenericInterface<LocalDate> interface as expected."

def addressContent = new String(Files.readAllBytes(addressPath))    
assert addressContent.contains('implements org.apache.avro.specific.SpecificRecord, pl.matsuo.interfacer.showcase.Entity')
println "Address class correctly implements Entity interface as expected."

def personContent = new String(Files.readAllBytes(personPath))    
assert personContent.contains('implements org.apache.avro.specific.SpecificRecord, pl.matsuo.interfacer.showcase.Entity')
println "Person class correctly implements Entity interface as expected."






