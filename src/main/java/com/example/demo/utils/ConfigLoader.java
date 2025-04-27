package com.example.demo.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static final Properties properties = new Properties();
    
    static {
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("Unable to find config.properties");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Error loading config.properties", e);
        }
    }
    
    public static String getEmailUsername() {
        return properties.getProperty("email.username");
    }
    
    public static String getEmailPassword() {
        return properties.getProperty("email.password");
    }
    
    public static String getSmtpHost() {
        return properties.getProperty("email.smtp.host");
    }
    
    public static String getSmtpPort() {
        return properties.getProperty("email.smtp.port");
    }
} 