package ru.em.kafkaappender.logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class KafkaAppenderLogger {
    public static final Logger INTERNAL_LOGGER = LogManager.getLogger("KafkaAppenderInternal");

    private KafkaAppenderLogger() {}

    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String NAME = "Kafka Appender: ";

    public static void info(String message, Object... args) {
        INTERNAL_LOGGER.info(GREEN + NAME + message + RESET, args);
    }

    public static void warn(String message, Object... args) {
        INTERNAL_LOGGER.info(YELLOW + NAME + message + RESET, args);
    }

    public static void error(String message, Object... args) {
        INTERNAL_LOGGER.info(RED + NAME + message + RESET, args);
    }

    public static void stackTrace(String message) {
        INTERNAL_LOGGER.info(RED + message + RESET);
    }
}
