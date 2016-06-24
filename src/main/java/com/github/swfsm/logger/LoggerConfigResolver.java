package com.github.swfsm.logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class LoggerConfigResolver {

    private static final String PROPERTIES_FILE = "junit-fail-logger.properties";
    private static final String FALLBACK_PROPERTIES_FILE = "junit-fail-logger-fallback.properties";

    private static final String METHOD_NAME_PROPERTY = "jufl.method";
    private static final String MESSAGE_PROPERTY = "jufl.message";

    public LoggerConfig resolve() {
        LoggerConfig config = new LoggerConfig();


        fromProperties(System.getProperties(), config);
        fromResource(PROPERTIES_FILE, config);

        fromResource(FALLBACK_PROPERTIES_FILE, config);
        if (!verify(config)) {
           throw new IllegalStateException("Unable to load properties from " + FALLBACK_PROPERTIES_FILE);
        }

        return config;
    }

    private LoggerConfig fromResource(String resourceName, LoggerConfig config) {

        try (InputStream in = getClass().getClassLoader().getResourceAsStream(resourceName)) {

            if (in == null) {
                return config;
            }

            Properties props = new Properties();
            props.load(in);

            fromProperties(props, config);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return config;
    }

    private void fromProperties(Properties properties, LoggerConfig config) {
        if (config.getMethod() == null) {
            config.setMethod(properties.getProperty(METHOD_NAME_PROPERTY));
        }

        if (config.getMessage() == null) {
            config.setMessage(properties.getProperty(MESSAGE_PROPERTY));
        }
    }

    private boolean verify(LoggerConfig loggerConfig) {
        return loggerConfig != null
                && loggerConfig.getMethod() != null && !loggerConfig.getMethod().isEmpty()
                && loggerConfig.getMessage() != null && !loggerConfig.getMessage().isEmpty();
    }

}
