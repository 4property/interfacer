# Interfacer maven plugin

Add interfaces to classes generated by other plugins.

## Why?

Let's suppose that you generate classes from a meta model. It may be avro or
protobuf schemas. Let's suppose that many generated classes have common api
that you would like to use in a generic way. What would be helpful here is
if all of these classes would implement same interface. If a tool that
generates classes does not have the capability, this plugin may help you.

**Simplified example:**

```java
// src/main/java manually defined interface
public interface HasName {
  String getName();
}

// target/generated-sources/avro
public class Person {

  String name;

  public String getName() {
    return name;
  }
  // [...]
}

public class Company {

  String name;

  public String getName() {
    return name;
  }
  // [...]
}

// after this plugin run

// target/generated-sources/avro
public class Person implements HasName {

  String name;

  public String getName() {
    return name;
  }
  // [...]
}

public class Company implements HasName {

  String name;

  public String getName() {
    return name;
  }
  // [...]
}
```

## Maven Usage

```xml
<plugin>
    <groupId>pl.matsuo.interfacer</groupId>
    <artifactId>interfacer-maven-plugin</artifactId>
    <version>0.0.7</version>
    <executions>
        <execution>
            <configuration>

                <!--
                     Use interfaces that are found in this directory. Plugin
                     assumes this is source directory, so it should contain
                     .java files for interfaces we want to add to classes.
                -->
                <interfacesDirectory>${project.basedir}/src/main/java</interfacesDirectory>

                <!--
                     Use interfaces that are found on the plugin's classpath
                     under specified package.
                -->
                <interfacePackage>pl.matsuo.interfacer.showcase</interfacePackage>

                <!--
                     Default: ${project.build.directory}/generated-sources/avro

                     Process classes found in this. Plugin assumes it
                     contains .java files. Classes defined in these files will
                     be parsed. If some class matches one of the interfaces
                     defined in interfaces sources, this interface will be added
                     and target .java file will be modified.
                -->
                <scanDirectory>${project.build.directory}/generated-sources/avro</scanDirectory>

            </configuration>
            <goals>
                <goal>add-interfaces</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

## Gradle Usage

```groovy
plugins {
    id 'pl.matsuo.interfacer'
}

interfacer {
    interfacePackage = 'pl.matsuo.interfacer.showcase'
    interfacesDirectory = file(path_to_custom_source_dir)
    scanDirectory = file(path_to_generated_classes_dir)
}
```

## Development and publishing

**Maven**

```sh
mvn clean
mvn -P release release:prepare
mvn -P release release:perform
```

**Gradle**

```sh
cd interfacer-gradle-plugin/
./gradlew publishPlugins
```
