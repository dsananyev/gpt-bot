package com.dsa.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.input.ClassLoaderObjectInputStream;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class PropertiesLoader {
    private static final Properties properties = new Properties();

    static {
        try (InputStream inputStream = PropertiesLoader.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (inputStream != null) {
                properties.load(inputStream);
            } else {
                throw new FileNotFoundException("Файл application.properties не найден");
            }
        } catch (IOException e) {
            throw new RuntimeException("Не удалось загрузить файл application.properties");
        }
    }

    public String getProperty(String name) {
        return System.getenv(name);
    }
}
