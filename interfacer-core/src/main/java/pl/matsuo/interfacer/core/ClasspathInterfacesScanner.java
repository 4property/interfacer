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
import java.util.Collection;
import java.util.List;
import pl.matsuo.interfacer.core.log.Log;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import pl.matsuo.interfacer.model.ifc.ClassIfcResolve;
import pl.matsuo.interfacer.model.ifc.IfcResolve;

/**
 * Implements scanning classpath for interfaces that should be used during
 * interface adding.
 */
public class ClasspathInterfacesScanner {

  /**
   * Use {@link Reflections} to scan for all interfaces in specified package
   * <code>interfacePackage
   * </code> and create {@link IfcResolve} instances for every one of them.
   */
  public List<IfcResolve> scanInterfacesFromClasspath(ClassLoader classLoader, String interfacePackages,
      TypeSolver typeSolver) {
    if (interfacePackages == null || interfacePackages.isEmpty()) {
      return emptyList();
    }

    String[] interfacePackagesArray = interfacePackages.split(",");
    Reflections reflections = createReflections(classLoader, interfacePackagesArray);

    return filterMap(reflections.getSubTypesOf(Object.class), type -> processClassFromClasspath(type, typeSolver));
  }

  /**
   * Create {@link IfcResolve} for <code>type</code> if it is representing
   * interface.
   */
  public IfcResolve processClassFromClasspath(Class<?> type, TypeSolver typeSolver) {
    if (type.isInterface()) {
      Log.info(() -> "[ClasspathInterfacesScanner]  Detected interface: %s".formatted(type.getCanonicalName()));
      return new ClassIfcResolve(type, typeSolver);
    }

    return null;
  }

  /** Create {@link Reflections} scanner. */
  public Reflections createReflections(ClassLoader classLoader, String[] interfacePackages) {
    Log.info(() -> "[ClasspathInterfacesScanner] Setting up reflection based scanning.");
    FilterBuilder filterBuilder = new FilterBuilder();
    List<URL> urls = new ArrayList<>();
    for (String interfacePackage : interfacePackages) {
      Log.debug(
          () -> "[ClasspathInterfacesScanner] Added package %s for reflection based scan.".formatted(interfacePackage));
      filterBuilder = filterBuilder.includePackage(interfacePackage);
      Collection<URL> packageUrls = ClasspathHelper.forPackage(interfacePackage, classLoader);
      if (Log.isDebugEnabled()) {
        Log.debug(() -> "[ClasspathInterfacesScanner] Found URLs for target package: %s".formatted(interfacePackage));
        packageUrls
            .forEach(url -> Log.debug(() -> "-------------------------> Added url: %s".formatted(url.getPath())));
      }
      urls.addAll(packageUrls);
    }

    return new Reflections(new ConfigurationBuilder().addClassLoaders(classLoader).setUrls(urls)
        .setScanners(Scanners.SubTypes.filterResultsBy(s -> true)).filterInputsBy(filterBuilder));
  }

  /** Create classloader based on <code>compileClasspathElements</code> urls. */
  public static ClassLoader getCompileClassLoader(List<String> compileClasspathElements) {
    if (compileClasspathElements.isEmpty()) {
      return ClasspathInterfacesScanner.class.getClassLoader();
    }
    Log.info(
        () -> "[ClasspathInterfacesScanner] Creating URLClassloader from maven or gradle plugin provided class path.");
    List<URL> jars = map(compileClasspathElements, ClasspathInterfacesScanner::toUrl);
    jars.forEach(element -> Log.info(() -> "[ClasspathInterfacesScanner] Adding classloader entry: " + element));

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
