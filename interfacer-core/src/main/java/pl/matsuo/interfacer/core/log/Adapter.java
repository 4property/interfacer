package pl.matsuo.interfacer.core.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/** Adapter for logging messages. To be used with maven or gradle plugins. */
public interface Adapter {

  void info(Supplier<String> message);

  void warn(Supplier<String> message);

  void error(Supplier<String> messageSupplier);

  void error(Supplier<String> messageSupplier, Supplier<Throwable> throwableSupplier);

  void warn(Supplier<String> messageSupplier, Supplier<Throwable> throwableSupplier);

  void debug(Supplier<String> message);

  boolean isDebugEnabled();

  boolean isErrorEnabled();

  boolean isInfoEnabled();

  boolean isWarnEnabled();

  boolean isTraceEnabled();

  /**
   * Returns a new Sl4j logging adapter if running directly from the IDE.
   * 
   * @param name A unique name for the logging adapter
   */
  static Adapter of(String name) {
    return new Adapter() {
      final Logger log = LoggerFactory.getLogger(name);


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
        return log.isTraceEnabled();
      }

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


    };
  }
}
