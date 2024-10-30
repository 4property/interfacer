package pl.matsuo.interfacer.gradle

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property

import javax.inject.Inject

class InterfacerExtension {

    final Property<File> interfacesDirectory
    final Property<File> scanDirectory
    final Property<String> interfacePackage
    final Property<String> languageLevel
    
    @Inject
    InterfacerExtension(ObjectFactory objects) {
        interfacesDirectory = objects.property(File)
        scanDirectory = objects.property(File)
        interfacePackage = objects.property(String)
        languageLevel = objects.property(String)
    }
}
