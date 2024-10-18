package me.pixel.perk;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.io.File;

import net.minecraft.entity.player.PlayerEntity;

import static me.pixel.perk.configs.*;

public class capeManagement {

    public static Set<UUID> checkedPlayers = new HashSet<>();


    //Dateien werden runtergeladen(nur txt files) und wenn directory net da is, wirds halt erstellt
    public static void downloadFiles() {
        Path configPath = Paths.get(CONFIG_PATH);
        try {
            if (!Files.exists(configPath)) {
                Files.createDirectories(configPath);
            }
            FileManagement.downloadFile(CAPE_LINKS_URL, CONFIG_PATH + CAPE_LINKS_FILE);
            FileManagement.downloadFile(CAPE_OWNERS_URL, CONFIG_PATH + CAPE_OWNERS_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Spieler überprüfen und Cape zuweisen
    public static void checkPlayer(PlayerEntity player) {
        UUID playerUUID = player.getUuid();

        // Datei 1: UUID -> Cape Name
        String capeName = getCapeNameFromUuid(playerUUID);
        if (capeName != null) {
            // Datei 2: Cape Name -> URL
            String capeUrl = getCapeUrlFromName(capeName);
            if (capeUrl != null) {
                assignCapeToPlayer(player, capeUrl);
            }
        }

        // Spieler zur Liste hinzufügen, um ihn nicht erneut zu überprüfen
        checkedPlayers.add(playerUUID);
    }


    // Suche den Cape-Namen anhand der UUID des Spielers in Datei 1
    private static String getCapeNameFromUuid(UUID uuid) {
        File file = new File("config/pixelcapes/uuid-capes.txt");
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            for (String line : lines) {
                String[] parts = line.split(" ");
                if (parts.length == 2 && parts[0].equals(uuid.toString())) {
                    return parts[1]; // Cape Name
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    // Suche die Cape-URL anhand des Cape-Namens in Datei 2
    private static String getCapeUrlFromName(String capeName) {
        File file = new File("config/pixelcapes/capes-list.txt");
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            for (String line : lines) {
                String[] parts = line.split(" ");
                if (parts.length == 2 && parts[0].equals(capeName)) {
                    return parts[1]; // Cape URL
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    // Weise dem Spieler das Cape zu
    private static void assignCapeToPlayer(PlayerEntity player, String capeUrl) {
        // TODO: Implementiere die Logik, um das Cape über die URL zuzuweisen
        System.out.println("Weise Cape zu: " + capeUrl + " für Spieler " + player.getName());
    }
}