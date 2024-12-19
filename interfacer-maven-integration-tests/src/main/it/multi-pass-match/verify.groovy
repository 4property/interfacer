import java.nio.file.Files
import java.nio.file.Paths

// Check if the build was successful
def log = new File(basedir, 'build.log')
assert log.exists()
assert log.text.contains('BUILD SUCCESS')

// Path to the generated BasicKeyValue class
def basicKeyValuePath = Paths.get(basedir.toString(), 'target', 'generated-sources', 'avro', 'pl', 'matsuo', 'interfacer', 'avro', 'BasicKeyValue.java')
// Check if the generated BasicKeyValue class exists
assert Files.exists(basicKeyValuePath)

// Path to the generated KeyValueReference class
def keyValueReferencePath = Paths.get(basedir.toString(), 'target', 'generated-sources', 'avro', 'pl', 'matsuo', 'interfacer', 'avro', 'KeyValueReference.java')
assert Files.exists(keyValueReferencePath)

// Read the content of the generated BasicKeyValue class and check if it implements IKeyValue
def basicKeyValueContent = new String(Files.readAllBytes(basicKeyValuePath))    
assert basicKeyValueContent.contains('implements org.apache.avro.specific.SpecificRecord, pl.matsuo.interfacer.showcase.IKeyValue')
println "BasicKeyValue class correctly implements IKeyValue interface. This happens only on the first pass of the interfacer scan."

// Read the content of the generated KeyValueReference class and check if it implements IKeyValueProvider. This would happen on the second pass.
def keyValueReferenceContent = new String(Files.readAllBytes(keyValueReferencePath))
assert keyValueReferenceContent.contains('implements org.apache.avro.specific.SpecificRecord, pl.matsuo.interfacer.showcase.HasKey, pl.matsuo.interfacer.showcase.IKeyValueProvider')
println "KeyValueReference class correctly implements HasKey and IKeyValueProvider interfaces. IKeyValue is implemented by the BasicKeyValue class, which is generated on the first pass of the interfacer scan. So IKeyValueProvider is implemented on the second pass of the interfacer scan."
