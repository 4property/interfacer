package pl.matsuo.interfacer.core;

import static java.util.Collections.emptyList;
import static pl.matsuo.core.util.collection.CollectionUtil.filterMap;
import static pl.matsuo.core.util.collection.CollectionUtil.map;

import com.github.javaparser.resolution.TypeSolver;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import pl.matsuo.interfacer.model.ifc.ClassIfcResolve;
import pl.matsuo.interfacer.model.ifc.IfcResolve;

/** Implements scanning classpath for interfaces that should be used during interface adding. */
@Slf4j
public class ClasspathInterfacesScanner {

  /**
   * Use {@link Reflections} to scan for all interfaces in specified package <code>interfacePackage
   * </code> and create {@link IfcResolve} instances for every one of them.
   */
  public List<IfcResolve> scanInterfacesFromClasspath(
      ClassLoader classLoader, String interfacePackages, TypeSolver typeSolver) {
    if (interfacePackages == null || interfacePackages.isEmpty()) {
      return emptyList();
    }

    String[] interfacePackagesArray = interfacePackages.split(",");
    Reflections reflections = createReflections(classLoader, interfacePackagesArray);

    return filterMap(
        reflections.getSubTypesOf(Object.class),
        type -> processClassFromClasspath(type, typeSolver));
  }

  /** Create {@link IfcResolve} for <code>type</code> if it is representing interface. */
  public IfcResolve processClassFromClasspath(Class<?> type, TypeSolver typeSolver) {
    log.info("Processing classpath type: " + type.getCanonicalName());
    if (type.isInterface()) {
      log.info("Adding interface: " + type.getName());
      return new ClassIfcResolve(type, typeSolver);
    }

    return null;
  }

  /** Create {@link Reflections} scanner. */
  public Reflections createReflections(ClassLoader classLoader, String[] interfacePackages) {
    FilterBuilder filterBuilder = new FilterBuilder();
    List<URL> urls = new ArrayList<>();
    for (String interfacePackage : interfacePackages) {
      filterBuilder = filterBuilder.includePackage(interfacePackage);
      urls.addAll(ClasspathHelper.forPackage(interfacePackage,classLoader));
    }


    return new Reflections(
        new ConfigurationBuilder()
            .addClassLoaders(classLoader)
            .setUrls(urls)
            .setScanners(Scanners.SubTypes.filterResultsBy(s -> true))
            .filterInputsBy(filterBuilder));
  }

  /** Create classloader based on <code>compileClasspathElements</code> urls. */
  public static ClassLoader getCompileClassLoader(List<String> compileClasspathElements) {
    List<URL> jars = map(compileClasspathElements, ClasspathInterfacesScanner::toUrl);
    jars.forEach(element -> log.info("Compile classloader entry: " + element));

    return new URLClassLoader(jars.toArray(new URL[0]));
  }

  /** Create url from file name. */
  public static URL toUrl(String name) {
    try {
      return new File(name).toURI().toURL();
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }
}
