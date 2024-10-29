package pl.matsuo.interfacer.core;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ParserConfiguration.LanguageLevel;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ClassLoaderTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import java.io.File;
import java.util.List;
import lombok.NonNull;

/** Data transfer object containing objects used for parsing. */
public class ParsingContext {

  final ClassLoader classLoader;
  final CombinedTypeSolver typeSolver;
  final ParserConfiguration parserConfiguration;
  final JavaParser javaParser;

  public ParsingContext(
      List<String> compileClasspathElements,
      @NonNull File scanDirectory,
      @NonNull File interfacesDirectory,
      @NonNull String languageLevel) {
    classLoader = ClasspathInterfacesScanner.getCompileClassLoader(compileClasspathElements);
    typeSolver = createTypeSolver(scanDirectory, interfacesDirectory, classLoader);
    parserConfiguration = new ParserConfiguration();
    parserConfiguration.setSymbolResolver(new JavaSymbolSolver(typeSolver));
    parserConfiguration.setLanguageLevel(getEnumFor(languageLevel));
    javaParser = new JavaParser(parserConfiguration);
  }

  /**
   * Convert language level string to {@link LanguageLevel} enum.
   *
   * @param languageLevel language level string
   * @return language level enum
   */
  private LanguageLevel getEnumFor(String languageLevel) {
    if (languageLevel == null || languageLevel.isEmpty()) {
      return LanguageLevel.CURRENT;
    }
    switch (languageLevel.toUpperCase()) {
      case "CURRENT" -> {
        return LanguageLevel.CURRENT;
      }
      case "POPULAR" -> {
        return LanguageLevel.POPULAR;
      }
      case "RAW" -> {
        return LanguageLevel.RAW;
      }
      case "LATEST" -> {
        return LanguageLevel.BLEEDING_EDGE;
      }
      default -> {
        if (languageLevel.contains(".")) {
          languageLevel = languageLevel.replace(".", "_");
        }
        languageLevel = "JAVA_" + languageLevel;
        return LanguageLevel.valueOf(languageLevel);
      }
    }
  }

  /**
   * Create type solver used for type resolution when checking is class matching
   * interface.
   */
  public CombinedTypeSolver createTypeSolver(
      File scanDirectory, File interfacesDirectory, ClassLoader classLoader) {
    CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
    combinedTypeSolver.add(new ClassLoaderTypeSolver(classLoader));
    combinedTypeSolver.add(new JavaParserTypeSolver(scanDirectory.toPath()));
    combinedTypeSolver.add(new JavaParserTypeSolver(interfacesDirectory.toPath()));
    return combinedTypeSolver;
  }
}
