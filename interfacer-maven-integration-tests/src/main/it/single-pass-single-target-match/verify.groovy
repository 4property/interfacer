import java.nio.file.Files
import java.nio.file.Paths

// Check if the build was successful
def log = new File(basedir, 'build.log')
assert log.exists()
assert log.text.contains('BUILD SUCCESS')

// Path to the generated ExampleRecord class
def generatedClassPath = Paths.get(basedir.toString(), 'target', 'generated-sources', 'avro', 'com', 'example', 'avro', 'ExampleRecord.java')

// Check if the generated ExampleRecord class exists
assert Files.exists(generatedClassPath)

// Read the content of the generated ExampleRecord class
def generatedClassContent = new String(Files.readAllBytes(generatedClassPath))

// Check if the generated class implements both Entity and Persona interfaces
assert generatedClassContent.contains('implements org.apache.avro.specific.SpecificRecord, com.example.Persona, com.example.Entity')

// Optionally, print a success message
println "ExampleRecord class correctly implements both Entity and Persona interfaces."
