package me.pixel.perk;

import net.fabricmc.loader.impl.util.log.Log;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class newcapes {
    public static final String MOD_ID = "capes";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final String CAPE_OWNERS_URL = "https://raw.githubusercontent.com/pixo2000/Mod-Data/main/capes/cape-zuweisung.txt";
    private static final String CAPES_URL = "https://raw.githubusercontent.com/pixo2000/Mod-Data/main/capes/cape-links.txt";

    private static final Map<UUID, String> OWNERS = new HashMap<>();
    private static final Map<String, String> URLS = new HashMap<>();
    private static final Map<String, Cape> TEXTURES = new HashMap<>();

    private static final List<Cape> TO_REGISTER = new ArrayList<>();
    private static final List<Cape> TO_RETRY = new ArrayList<>();
    private static final List<Cape> TO_REMOVE = new ArrayList<>();

    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    public static void init() {
        LOGGER.info("Loading capes...");
        OWNERS.clear();
        URLS.clear();
        TEXTURES.clear();
        TO_REGISTER.clear();
        TO_RETRY.clear();
        TO_REMOVE.clear();

        executorService.execute(() -> {
            // Cape owners
            List<String> lines = sendHttpRequest(CAPE_OWNERS_URL);
            if (lines != null) lines.forEach(s -> {
                String[] split = s.split(" ");
                LOGGER.info("Cape owner: " + s);

                if (split.length >= 2) {
                    OWNERS.put(UUID.fromString(split[0]), split[1]);
                    if (!TEXTURES.containsKey(split[1])) TEXTURES.put(split[1], new Cape(split[1]));
                    LOGGER.info("got owner uuid");
                }
            });

            // Capes
            lines = sendHttpRequest(CAPES_URL);
            if (lines != null) lines.forEach(s -> {
                String[] split = s.split(" ");

                if (split.length >= 2) {
                    if (!URLS.containsKey(split[0])) URLS.put(split[0], split[1]);
                    LOGGER.info("got cape url");
                }
            });
        });
    }

    public static void onTick(MinecraftClient minecraftClient) {
        LOGGER.info("Tick");
        synchronized (TO_REGISTER) {
            for (Cape cape : TO_REGISTER) cape.register();
            TO_REGISTER.clear();
        }

        synchronized (TO_RETRY) {
            TO_RETRY.removeIf(Cape::tick);
        }

        synchronized (TO_REMOVE) {
            for (Cape cape : TO_REMOVE) {
                URLS.remove(cape.name);
                TEXTURES.remove(cape.name);
                TO_REGISTER.remove(cape);
                TO_RETRY.remove(cape);
            }

            TO_REMOVE.clear();
        }
        LOGGER.info("Tick end");
    }

    public static Identifier get(UUID player) {
        LOGGER.info("Cape Start identifier");
        String capeName = OWNERS.get(player);
        LOGGER.info("Cape Start identifier2");
        if (capeName != null) {
            LOGGER.info("Cape Start identifier3");
            Cape cape = TEXTURES.get(capeName);
            LOGGER.info("Cape Start identifier4");
            if (cape == null){
                LOGGER.info("Cape End identifier False2");
                return null;
            }

            if (cape.isDownloaded()) return cape.identifier;

            cape.download();
            LOGGER.info("Cape End identifier True");
            return null;
        }
        LOGGER.info("Cape End identifier False");
        return null;
    }

    private static List<String> sendHttpRequest(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                return in.lines().collect(Collectors.toList());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static class Cape {
        public static final String MOD_ID = "capes";
        public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

        public Identifier identifier;
        private static int COUNT = 0;

        private final String name;

        private boolean downloaded;
        private boolean downloading;

        private NativeImage img;

        private int retryTimer;

        public Cape(String name) {
            identifier = new Identifier("capes", String.valueOf(COUNT++));

            this.name = name;
        }

        public void download() {
            if (downloaded || downloading || retryTimer > 0) return;
            downloading = true;

            executorService.execute(() -> {
                try {
                    String url = URLS.get(name);
                    if (url == null) {
                        synchronized (TO_RETRY) {
                            TO_REMOVE.add(this);
                            downloading = false;
                            return;
                        }
                    }

                    InputStream in = new URL(url).openStream();
                    if (in == null) {
                        synchronized (TO_RETRY) {
                            TO_RETRY.add(this);
                            retryTimer = 10 * 20;
                            downloading = true;
                            return;
                        }
                    }

                    img = NativeImage.read(in);

                    synchronized (TO_REGISTER) {
                        TO_REGISTER.add(this);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        public void register() {
            MinecraftClient.getInstance().getTextureManager().registerTexture(identifier, new NativeImageBackedTexture(img));
            img = null;

            downloading = false;
            downloaded = true;
        }

        public boolean tick() {
            if (retryTimer > 0) {
                retryTimer--;
            } else {
                download();
                return true;
            }

            return false;
        }

        public boolean isDownloaded() {
            return downloaded;
        }
    }
}