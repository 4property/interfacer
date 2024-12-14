package pl.matsuo.interfacer.core.log;

import java.util.function.Supplier;

public class Log {
    static Adapter adapter ;

    static {
        adapter =  Adapter.of("pl.matsuo.interfacer");
    }

    /**
     * Set the adapter to be used for logging. By default, it is set to the adapter for the
     * pl.matsuo.interfacer package that uses slf4j.
     * 
     * @param adapter
     */
    public static void setAdapter(Adapter adapter) {
        Log.adapter = adapter;
    }
    
    public static void info(Supplier<String> message) {
        adapter.info(message);
    }

    public static void warn(Supplier<String> message) {
        adapter.warn(message);
    }

    public static void warn(Supplier<String> messageSupplier, Supplier<Throwable> throwableSupplier) {
        adapter.warn(messageSupplier, throwableSupplier);
    }

    public static void error(Supplier<String> messageSupplier) {
        adapter.error(messageSupplier);
    }

    public static void error(Supplier<String> messageSupplier, Supplier<Throwable> throwableSupplier) {
        adapter.error(messageSupplier, throwableSupplier);
    }

    public static void debug(Supplier<String> message) {
        adapter.debug(message);
    }

    public static boolean isDebugEnabled() {
        return adapter.isDebugEnabled();
    }

    public static boolean isErrorEnabled(){
        return adapter.isErrorEnabled();
    }

    public static boolean isWarnEnabled(){
        return adapter.isWarnEnabled();
    }

    public static boolean isInfoEnabled(){
        return adapter.isInfoEnabled();
    }
}
