package pl.matsuo.interfacer.core;

import com.google.common.base.Supplier;

/** Adapter for logging messages. To be used with maven or gradle plugins. */
public interface LogAdapter {

  void info(Supplier<String> message);

  void warn(Supplier<String> message);

  void error(Supplier<String> message);

}
