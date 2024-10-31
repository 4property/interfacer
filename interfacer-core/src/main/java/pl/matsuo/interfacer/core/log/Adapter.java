package pl.matsuo.interfacer.core.log;

import com.google.common.base.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Adapter for logging messages. To be used with maven or gradle plugins. */
public interface Adapter {

  void info(Supplier<String> message);

  void warn(Supplier<String> message);

  void error(Supplier<String> messageSupplier);

  void error(Supplier<String> messageSupplier, Supplier<Throwable> throwableSupplier);

  void warn(Supplier<String> messageSupplier, Supplier<Throwable> throwableSupplier);

  void debug(Supplier<String> message);

  /**
   * Returns a new Sl4j logging adapter if running directly from the IDE.
   * 
   * @param name
   * @return
   */
  static Adapter of(String name) {
    return new Adapter() {
      Logger log = LoggerFactory.getLogger(name);

      @Override
      public void debug(Supplier<String> message) {
        log.debug(message.get());
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