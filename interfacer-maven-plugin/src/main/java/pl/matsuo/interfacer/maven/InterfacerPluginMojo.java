package pl.matsuo.interfacer.maven;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import pl.matsuo.interfacer.core.InterfacesAdder;

/**
 * This plugin is a sample for building your own plugins. It takes a directory of source code and
 * adds a trace line to each method.
 */
@Mojo(
        name = "add-interfaces",
        defaultPhase = LifecyclePhase.PROCESS_SOURCES,
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME,
        threadSafe = true)
public class InterfacerPluginMojo extends AbstractMojo {

    /**
     * Package aggregating interfaces that should be scanned through entities.
     */
    @Parameter(property = "interfacePackage", required = true)
    String interfacePackage;

    /**
     * Language level to use when parsing source files. The default is is POPULAR (Java 11)
     */
    @Parameter(defaultValue = "POPULAR")
    String languageLevel;

    /**
     * Location for the source files with added trace lines.
     */
    @Parameter(defaultValue = "${project.build.sourceDirectory}")
    File interfacesDirectory;

    /**
     * Location where the modified source files should be saved.
     */
    @Parameter(defaultValue = "${project.build.directory}/generated-sources/avro")
    File scanDirectory;

    /**
     * The current Maven project.
     */
    @Parameter(property = "project", required = true, readonly = true)
    MavenProject project;

    /**
     * Configure execution environment and invoke interface adder.
     */
    @Override
    public void execute() throws MojoExecutionException {
        JavaParserMavenUtils.makeJavaParserLogToMavenOutput(getLog());
        JavaParserMavenUtils.makeInterfacerLogToMavenOutput(getLog());

        try {
            new InterfacesAdder()
                    .addInterfacesAllFiles(
                            scanDirectory,
                            interfacesDirectory,
                            interfacePackage,
                            languageLevel,
                            getCombinedClasspathElements());
        } catch (Exception e) {
            throw new MojoExecutionException("Error occurred: " + e.getMessage(), e);
        }
    }

    private List<String> getCombinedClasspathElements() throws DependencyResolutionRequiredException {
        Set<String> combinedClassPathElements = new HashSet<>(project.getCompileClasspathElements());
        combinedClassPathElements.addAll(project.getRuntimeClasspathElements());
        combinedClassPathElements.addAll(project.getCompileSourceRoots());
        getLog().info("Processed class path elements");
        return combinedClassPathElements.stream().peek(s -> getLog().debug(s + '\n')).toList();
    }
}
