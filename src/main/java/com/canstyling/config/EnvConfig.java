package com.canstyling.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Lector nativo y robusto de variables de entorno (.env).
 * Permite compatibilidad total sin requerir librerías externas para la ejecución estándar.
 */
public class EnvConfig {

    private static final Map<String, String> envVars = new HashMap<>();

    static {
        loadEnv();
    }

    private static void loadEnv() {
        File envFile = new File(".env");
        if (!envFile.exists()) {
            envFile = new File("..\\.env");
        }
        if (envFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(envFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) {
                        continue;
                    }
                    int equalsIdx = line.indexOf('=');
                    if (equalsIdx > 0) {
                        String key = line.substring(0, equalsIdx).trim();
                        String value = line.substring(equalsIdx + 1).trim();
                        envVars.put(key, value);
                    }
                }
            } catch (IOException e) {
                System.err.println("Advertencia: No se pudo leer el archivo .env: " + e.getMessage());
            }
        }
    }

    public static String get(String key, String defaultValue) {
        String systemEnv = System.getenv(key);
        if (systemEnv != null && !systemEnv.isEmpty()) {
            return systemEnv;
        }
        return envVars.getOrDefault(key, defaultValue);
    }

    public static String get(String key) {
        return get(key, "");
    }

    public static int getInt(String key, int defaultValue) {
        String val = get(key, String.valueOf(defaultValue));
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
