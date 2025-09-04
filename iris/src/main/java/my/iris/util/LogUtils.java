package my.iris.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public record LogUtils() {

    enum LogLevel {
        ERROR, INFO, WARN
    }

    static void log(LogLevel level, Class<?> clazz, Object message) {
        log(level, clazz, message, null);
    }

    static void log(LogLevel level, Class<?> clazz, Object message, Throwable t) {
        Log log = LogFactory.getLog(clazz);
        switch (level) {
            case ERROR -> logError(log, message, t);
            case INFO -> logInfo(log, message, t);
            case WARN -> logWarn(log, message, t);
        }
    }

    private static void logError(Log log, Object message, Throwable t) {
        if (t == null) {
            log.error(message);
        } else {
            log.error(message, t);
        }
    }

    private static void logInfo(Log log, Object message, Throwable t) {
        if (t == null) {
            log.info(message);
        } else {
            log.info(message, t);
        }
    }

    private static void logWarn(Log log, Object message, Throwable t) {
        if (t == null) {
            log.warn(message);
        } else {
            log.warn(message, t);
        }
    }

    // Convenience methods for each level
    public static void error(Class<?> clazz, Object message) {
        log(LogLevel.ERROR, clazz, message);
    }

    public static void error(Class<?> clazz, Object message, Throwable t) {
        log(LogLevel.ERROR, clazz, message, t);
    }

    public static void info(Class<?> clazz, Object message) {
        log(LogLevel.INFO, clazz, message);
    }

    public static void info(Class<?> clazz, Object message, Throwable t) {
        log(LogLevel.INFO, clazz, message, t);
    }

    public static void warn(Class<?> clazz, Object message) {
        log(LogLevel.WARN, clazz, message);
    }

    public static void warn(Class<?> clazz, Object message, Throwable t) {
        log(LogLevel.WARN, clazz, message, t);
    }
}
