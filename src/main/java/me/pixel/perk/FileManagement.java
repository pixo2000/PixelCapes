package me.pixel.perk;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;

public class FileManagement {
    public static void downloadFile(String url, String destination) {
        try {
            File destFile = new File(destination);
            FileUtils.copyURLToFile(new URL(url), destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void clearFiles(String configPath) {
        try {
            Path path = Paths.get(configPath);
            if (Files.exists(path)) {
                Files.walk(path)
                     .filter(Files::isRegularFile)
                     .forEach(file -> {
                         try {
                             Files.delete(file);
                         } catch (IOException e) {
                             e.printStackTrace();
                         }
                     });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}