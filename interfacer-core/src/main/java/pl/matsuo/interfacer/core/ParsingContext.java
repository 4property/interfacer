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
import pl.matsuo.interfacer.core.log.Log;

/** Data transfer object containing objects used for parsing. */
public class ParsingContext {

  final ClassLoader classLoader;
  final CombinedTypeSolver typeSolver;
  final ParserConfiguration parserConfiguration;
  final JavaParser javaParser;

  public ParsingContext(
      ClassLoader compileClasspathElementsLoader,
      @NonNull File scanDirectory,
      @NonNull List<File> interfacesDirectories,
      @NonNull String languageLevel) {
    classLoader = compileClasspathElementsLoader;
    typeSolver = createCombinedSolver(scanDirectory, interfacesDirectories, classLoader, languageLevel);
    parserConfiguration = new ParserConfiguration()
        .setSymbolResolver(new JavaSymbolSolver(typeSolver))
        .setLanguageLevel(getEnumFor(languageLevel))
        .setLexicalPreservationEnabled(true);
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
      Log.debug(() -> "Language level is empty, using CURRENT");
      return LanguageLevel.CURRENT;
    }
    var result = switch (languageLevel.toUpperCase()) {
      case "CURRENT" -> LanguageLevel.CURRENT;
      case "POPULAR" -> LanguageLevel.POPULAR;
      case "RAW" -> LanguageLevel.RAW;
      case "LATEST" -> LanguageLevel.BLEEDING_EDGE;
      default -> {
        if (languageLevel.contains(".")) {
          languageLevel = languageLevel.replace(".", "_");
        }
        languageLevel = "JAVA_" + languageLevel;
        yield LanguageLevel.valueOf(languageLevel);
      }
    };
    Log.debug(() -> "Language level: %s".formatted(result));
    return result;
  }

  /**
   * Create type solver used for type resolution when checking is class matching
   * interface.
   */
  private CombinedTypeSolver createCombinedSolver(
      File scanDirectory, List<File> interfacesDirectories, ClassLoader classLoader, String languageLevel) {
    CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
    combinedTypeSolver.add(new ClassLoaderTypeSolver(classLoader));
    ParserConfiguration parserConfiguration = new ParserConfiguration().setLanguageLevel(getEnumFor(languageLevel))
        .setLexicalPreservationEnabled(true);
    combinedTypeSolver.add(new JavaParserTypeSolver(scanDirectory.toPath(), parserConfiguration));
    for (File interfacesDirectory : interfacesDirectories) {
      combinedTypeSolver.add(new JavaParserTypeSolver(interfacesDirectory.toPath(), parserConfiguration));
    }
    return combinedTypeSolver;
  }
}
