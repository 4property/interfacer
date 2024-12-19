package pl.matsuo.interfacer.maven;

import java.util.function.Supplier;
import org.apache.maven.plugin.logging.Log;

/** Things to make JavaParser and Maven interact better */
public class JavaParserMavenUtils {
  public static void makeJavaParserLogToMavenOutput(Log log) {
    com.github.javaparser.utils.Log.setAdapter(new com.github.javaparser.utils.Log.Adapter() {
      @Override
      public void info(Supplier<String> message) {
        log.info(message.get());
      }

      @Override
      public void trace(Supplier<String> message) {
        log.debug(message.get());
      }

      @Override
      public void error(Supplier<Throwable> throwableSupplier, Supplier<String> messageSupplier) {
        log.error(messageSupplier.get(), throwableSupplier.get());
      }
    });
  }

  /** Make interfacer log to Maven output. */
  public static void makeInterfacerLogToMavenOutput(Log log) {
    pl.matsuo.interfacer.core.log.Log.setAdapter(new pl.matsuo.interfacer.core.log.Adapter() {
      @Override
      public void info(Supplier<String> message) {
        log.info(message.get());
      }

      @Override
      public void warn(Supplier<String> message) {
        log.warn(message.get());
      }

      @Override
      public void error(Supplier<String> messageSupplier) {
        log.error(messageSupplier.get());
      }

      @Override
      public void error(Supplier<String> messageSupplier, Supplier<Throwable> throwableSupplier) {
        log.error(messageSupplier.get(), throwableSupplier.get());
      }

      @Override
      public void warn(Supplier<String> messageSupplier, Supplier<Throwable> throwableSupplier) {
        log.warn(messageSupplier.get(), throwableSupplier.get());
      }

      @Override
      public void debug(Supplier<String> message) {
        log.debug(message.get());
      }

      @Override
      public boolean isDebugEnabled() {
        return log.isDebugEnabled();
      }

      @Override
      public boolean isErrorEnabled() {
        return log.isErrorEnabled();
      }

      @Override
      public boolean isInfoEnabled() {
        return log.isInfoEnabled();
      }

      @Override
      public boolean isWarnEnabled() {
        return log.isWarnEnabled();
      }

      @Override
      public boolean isTraceEnabled() {
        return log.isDebugEnabled();
      }

    });
  }
}
