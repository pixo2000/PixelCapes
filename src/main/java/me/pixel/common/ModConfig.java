package me.pixel.common;

import java.nio.file.Files;
import java.nio.file.Path;

public class ModConfig {

    public static void loadConfig() {
        Path configPath = Path.of("config", "beispielmod.json");
        if (!Files.exists(configPath)) {
            // Create default config
        } else {
            // Load existing config
        }
    }
}
