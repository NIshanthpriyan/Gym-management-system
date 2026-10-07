package com.gym;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.nio.file.Files;

@SpringBootApplication
public class GymManagementSystemApplication {

    public static void main(String[] args) {
        loadDotEnv();
        SpringApplication.run(GymManagementSystemApplication.class, args);
    }

    private static void loadDotEnv() {
        File envFile = new File(".env");
        if (envFile.exists()) {
            try {
                for (String line : Files.readAllLines(envFile.toPath())) {
                    line = line.trim();
                    if (!line.isEmpty() && !line.startsWith("#") && line.contains("=")) {
                        int idx = line.indexOf('=');
                        String key = line.substring(0, idx).trim();
                        String value = line.substring(idx + 1).trim();
                        if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) {
                            value = value.substring(1, value.length() - 1);
                        }
                        System.setProperty(key, value);
                    }
                }
            } catch (Exception ignored) {}
        }
    }
}
