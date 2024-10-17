package me.pixel.perk;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Stream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static me.pixel.perk.serverconnection.CONFIG_PATH;

public class CapeAPI {
    public static final CapeAPI INSTANCE = new CapeAPI();
    private final List<IdentifiedCape> cachedIdentifiedCapes = new ArrayList<>();
    public final String MOD_ID = "capes";
    public final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static final String CAPE_OWNERS_FILE = CONFIG_PATH + "/cape-zuweisung.txt";
    private final String CAPES_FILE = CONFIG_PATH + "/cape-links.txt"; // datei verlinken
    private final Map<String, String> cachedURLs;
    private final List<String> cachedUsers;

    public CapeAPI() {
        this.cachedURLs = new HashMap<>();
        this.cachedUsers = new ArrayList<>();
    }

    public Identifier getIdentifiedCape(UUID id) {
        for (IdentifiedCape identifiedCape : cachedIdentifiedCapes) {
            if (identifiedCape.capeOwner.id().equals(id.toString())) {
                return identifiedCape.identifier;
            }
        }
        return null;
    }

    public String getCapeUrl(String name) {
        Stream<String> lines;
        try {
            lines = Files.lines(Paths.get(CAPES_FILE));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        if (lines != null) {
            lines.forEach(s -> {
                if (!s.isEmpty()) {
                    String[] split = s.split(" ");
                    cachedURLs.computeIfAbsent(split[0], k -> split[1]);
                }
            });
        }
        return cachedURLs.get(name);
    }

    public void register(IdentifiedCape identifiedCape) {
        Identifier id = identifiedCape.identifier;
        NativeImage img = identifiedCape.nativeImage;
        MinecraftClient.getInstance().getTextureManager().registerTexture(id, new NativeImageBackedTexture(img));
        synchronized (cachedIdentifiedCapes) {
            cachedIdentifiedCapes.add(identifiedCape);
        }
    }

    public boolean isCapeOwner(UUID id) {
        return cachedUsers.contains(id.toString());
    }

    public NativeImage getImageThroughURL(String URL) {
        NativeImage img;
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(URL).openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            if (connection.getResponseCode() == 200) {
                try (InputStream in = connection.getInputStream()) {
                    img = NativeImage.read(in);
                }
            } else {
                throw new IOException("Failed to fetch image, HTTP response code: " + connection.getResponseCode());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return img;
    }

    public void loadCapes() {
        cachedUsers.clear();
        cachedIdentifiedCapes.clear();
        cachedURLs.clear();
        Stream<String> lines;
        try {
            lines = Files.lines(Paths.get(CAPE_OWNERS_FILE));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        if (lines != null) {
            lines.forEach(s -> {
                if(s.isEmpty())
                    return;
                String[] split = s.split(" ");
                LOGGER.info("Player found(uuid + capename + ingamename): " + s);
                if (split.length >= 2) {
                    NativeImage img = getImageThroughURL(getCapeUrl(split[1]));
                    register(new IdentifiedCape(img, new CapeOwner(img, split[0])));
                    synchronized (cachedUsers) {
                        cachedUsers.add(split[0]);
                    }
                }
            });
        }
    }
}