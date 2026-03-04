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
        String systemProp = System.getProperty(key);
        if (systemProp != null && !systemProp.isBlank()) {
            return systemProp;
        }
        String fileProp = props.getProperty(key);
        if (fileProp != null && !fileProp.isBlank()) {
            return fileProp;
        }
        return defaultValue;
    }
}
