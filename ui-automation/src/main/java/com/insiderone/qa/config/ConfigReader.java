package com.insiderone.qa.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {

    private static final Properties props = new Properties();

    static {
        try (InputStream is = ConfigReader.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (is != null) {
                props.load(is);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    public static String getBaseUrl() {
        return resolve("base.url", "https://insiderone.com");
    }

    public static String getBrowser() {
        return resolve("browser", "chrome");
    }

    public static String getGridUrl() {
        String value = resolve("grid.url", null);
        return (value != null && !value.isBlank()) ? value : null;
    }

    public static int getExplicitWaitSeconds() {
        String value = resolve("explicit.wait.seconds", "15");
        return Integer.parseInt(value);
    }

    // System property takes precedence over config.properties
    private static String resolve(String key, String defaultValue) {
        // 1. Try env variable (KEY_NAME) - Best for Docker/CI
        String envKey = key.replace(".", "_").toUpperCase();
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }

        // 2. Try system property (-Dkey=value) - Best for IDE/Local
        String systemProp = System.getProperty(key);
        if (systemProp != null && !systemProp.isBlank()) {
            return systemProp;
        }

        // 3. Try config.properties
        String fileProp = props.getProperty(key);
        if (fileProp != null && !fileProp.isBlank()) {
            return fileProp;
        }

        return defaultValue;
    }
}
