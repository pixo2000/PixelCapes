package me.pixel.perk;

import me.pixel.meteor.Http;
import me.pixel.meteor.MeteorExecutor;
import net.fabricmc.loader.impl.util.log.Log;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Stream;


public class Capes {
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

    public static void init() {
        LOGGER.info("Loading capes...");
        OWNERS.clear();
        URLS.clear();
        TEXTURES.clear();
        TO_REGISTER.clear();
        TO_RETRY.clear();
        TO_REMOVE.clear();

        MeteorExecutor.execute(() -> {
            // Cape owners
            Stream<String> lines = Http.get(CAPE_OWNERS_URL).sendLines();
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
            lines = Http.get(CAPES_URL).sendLines();
            if (lines != null) lines.forEach(s -> {
                String[] split = s.split(" ");

                if (split.length >= 2) {
                    if (!URLS.containsKey(split[0])) URLS.put(split[0], split[1]);
                    LOGGER.info("got cape url");
                }
            });
        });
        onTick(MinecraftClient.getInstance());


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
            identifier = Identifier.of("capes/" + COUNT++);

            this.name = name;
        }

        public void download() {
            if (downloaded || downloading || retryTimer > 0) return;
            downloading = true;

            MeteorExecutor.execute(() -> {
                try {
                    String url = URLS.get(name);
                    if (url == null) {
                        synchronized (TO_RETRY) {
                            TO_REMOVE.add(this);
                            downloading = false;
                            return;
                        }
                    }

                    InputStream in = Http.get(url).sendInputStream();
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