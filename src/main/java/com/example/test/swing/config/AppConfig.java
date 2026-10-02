package com.example.test.swing.config;

import java.io.InputStream;
import java.util.Properties;

public class AppConfig {

    private static final Properties properties = new Properties();

    static {
        try (InputStream input = AppConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new IllegalStateException("O arquivo 'config.properties' não foi encontrado no classpath.");
            }
            properties.load(input);
        } catch (Exception e) {
            throw new RuntimeException("Falha ao carregar as configurações do arquivo 'config.properties'", e);
        }
    }

    public static String getApiBaseUrl() {
        String url = properties.getProperty("api.base.url");
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalStateException("A propriedade 'api.base.url' não está definida no arquivo config.properties.");
        }
        return url.trim();
    }
}