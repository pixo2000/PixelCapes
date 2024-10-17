package me.pixel.perk;

import net.fabricmc.api.ClientModInitializer;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.*;
import java.net.Socket;
import java.nio.file.*;

public class serverconnection implements ClientModInitializer {

    public static final String CONFIG_PATH = System.getProperty("user.home") + "/.pixelcapemod/config/";
    private static final String CAPE_OWNERS_URL = "https://raw.githubusercontent.com/pixo2000/Mod-Data/main/capes/cape-zuweisung.txt";
    private static final String CAPE_LINKS_URL = "https://raw.githubusercontent.com/pixo2000/Mod-Data/main/capes/cape-links.txt";
    private static final String SERVER_IP = "0.0.0.0";
    private static final int SERVER_PORT = 52798;

    private OkHttpClient client = new OkHttpClient();
    private Socket serverSocket;
    private PrintWriter serverOut;

    @Override
    public void onInitializeClient() {
        try {
            Path configPath = Paths.get(CONFIG_PATH);
            if (!Files.exists(configPath)) {
                Files.createDirectories(configPath);
            }

            downloadFile(CAPE_OWNERS_URL, CONFIG_PATH + "/cape-zuweisung.txt");
            downloadFile(CAPE_LINKS_URL, CONFIG_PATH + "/cape-links.txt");

            connectToServer();

            listenForUpdates();

        } catch (IOException e) {
            e.printStackTrace();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (serverOut != null) {
                serverOut.println("exit");
                serverOut.flush();
            }
            closeConnection();
        }));
    }

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

    private void listenForUpdates() {
        new Thread(() -> {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()))) {
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.equalsIgnoreCase("update")) {
                        System.out.println("Update-Befehl erhalten. Dateien werden neu heruntergeladen...");
                        downloadFile(CAPE_OWNERS_URL, CONFIG_PATH + "/cape-zuweisung.txt");
                        downloadFile(CAPE_LINKS_URL, CONFIG_PATH + "/cape-links.txt");
                        reloadCapes();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void reloadCapes() {
        CapeAPI.INSTANCE.loadCapes();
        System.out.println("Capes neu geladen!");
    }

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
