package me.pixel.perk;

import net.fabricmc.api.ClientModInitializer;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.*;
import java.net.Socket;
import java.nio.file.*;

import static me.pixel.perk.capeManagement.downloadFiles;
import static me.pixel.perk.FileManagement.clearFiles;
import static me.pixel.perk.configs.*;

public class serverConnectionManager implements ClientModInitializer {

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

            downloadFiles();

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
                        reloadCapes();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void reloadCapes() {
        clearFiles(CONFIG_PATH);
        downloadFiles();
        // jetzt noch capes neu laden
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