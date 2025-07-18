package com.dsa.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Properties;

@Slf4j
public class PropertiesLoader {
    private static final Properties properties = new Properties();
    private static final String customPath = System.getProperty("properties");

    static {
        boolean propertiesLoaded = false;

        if (customPath != null) {
            propertiesLoaded = tryLoadFromPath(customPath, "аргумент --Dproperties");
        }

        if (!propertiesLoaded) {
            try (InputStream inputStream = PropertiesLoader.class.getClassLoader()
                    .getResourceAsStream("application.properties")) {
                if (inputStream != null) {
                    properties.load(inputStream);
                    log.info("Got application.properties from classpath");
                } else {
                    log.warn("application.properties not found in classpath");
                }
            } catch (Exception e) {
                log.error("Failed to get application.properties from classpath", e);
            }
        }
    }

    private static boolean tryLoadFromPath(String path, String source) {
        try {
            File file = new File(path);
            if (file.exists()) {
                try (InputStream inputStream = new FileInputStream(file)) {
                    properties.load(inputStream);
                    log.info("Got application.properties from {}: {}", source, path);
                    return true;
                }
            } else {
                log.warn("File not found in {}: {}", source, path);
            }
        } catch (Exception e) {
            log.error("Failed to get application.properties from {}: {}", source, path, e);
        }
        return false;
    }

    public String getProperty(String name) {
        return properties.getProperty(name);
    }
}
