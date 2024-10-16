package me.pixel.perk;

import net.fabricmc.api.ClientModInitializer;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.*;
import java.net.Socket;
import java.nio.file.*;

public class serverconnection implements ClientModInitializer {

    private static final String CONFIG_PATH = System.getProperty("user.home") + "/minecraftordner/config/pixelcapes"; // ändern weil der pfad so net richtig is
    private static final String FILE1_URL = "https://raw.githubusercontent.com/user/repo/branch/file1.json"; // Link zu deiner GitHub-Datei 1
    private static final String FILE2_URL = "https://raw.githubusercontent.com/user/repo/branch/file2.json"; // Link zu deiner GitHub-Datei 2
    private static final String SERVER_IP = "127.0.0.1"; // IP deines Servers
    private static final int SERVER_PORT = 12345; // Port deines Servers

    private OkHttpClient client = new OkHttpClient();
    private Socket serverSocket;
    private PrintWriter serverOut;

    @Override
    public void onInitializeClient() {
        try {
            // Erstelle das Verzeichnis, wenn es nicht existiert
            Path configPath = Paths.get(CONFIG_PATH);
            if (!Files.exists(configPath)) {
                Files.createDirectories(configPath);
            }

            // Dateien beim Start downloaden
            downloadFile(FILE1_URL, CONFIG_PATH + "/file1.json");
            downloadFile(FILE2_URL, CONFIG_PATH + "/file2.json");

            // Verbindung zum Server herstellen und melden, dass der Client gestartet ist
            connectToServer();

            // Server nach Updates lauschen
            listenForUpdates();

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Bei Shutdown (Client wird geschlossen) dem Server melden
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (serverOut != null) {
                serverOut.println("exit");
                serverOut.flush();
            }
            closeConnection();
        }));
    }

    // Methode zum Download der Dateien
    private void downloadFile(String fileUrl, String outputPath) {
        Request request = new Request.Builder().url(fileUrl).build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                try (InputStream inputStream = response.body().byteStream();
                     FileOutputStream fileOutputStream = new FileOutputStream(outputPath)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, bytesRead);
                    }
                    System.out.println("Datei erfolgreich heruntergeladen: " + outputPath);
                }
            } else {
                System.out.println("Fehler beim Herunterladen der Datei: " + fileUrl);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Verbindung zum Server aufbauen
    private void connectToServer() {
        try {
            serverSocket = new Socket(SERVER_IP, SERVER_PORT);
            serverOut = new PrintWriter(serverSocket.getOutputStream(), true);
            serverOut.println("client_started");

            System.out.println("Verbindung zum Server hergestellt.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Updates vom Server empfangen
    private void listenForUpdates() {
        new Thread(() -> {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()))) {
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.equalsIgnoreCase("update")) {
                        System.out.println("Update-Befehl erhalten. Dateien werden neu heruntergeladen...");
                        downloadFile(FILE1_URL, CONFIG_PATH + "/file1.json");
                        downloadFile(FILE2_URL, CONFIG_PATH + "/file2.json");

                        // Hier kannst du den Code zum Neuladen der Capes einfügen
                        reloadCapes();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Capes neu laden (diese Methode musst du entsprechend der Cape-Logik implementieren)
    private void reloadCapes() {
        System.out.println("Capes neu geladen!");
        // Hier die Logik zum Neuladen der Capes implementieren
    }

    // Verbindung zum Server schließen
    private void closeConnection() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
