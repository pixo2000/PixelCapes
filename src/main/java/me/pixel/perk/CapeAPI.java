package me.pixel.perk;

import biz.source_code.base64Coder.Base64Coder;
import me.pixel.meteor.Http;
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

public class CapeAPI {
    private final ArrayList<IdentifiedCape> cachedIdentifiedCapes = new ArrayList<>();
    public final String MOD_ID = "capes";
    public final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static final String CAPE_OWNERS_URL = "https://raw.githubusercontent.com/pixo2000/Mod-Data/main/capes/cape-zuweisung.txt";
    private final String CAPES_URL = "https://raw.githubusercontent.com/pixo2000/Mod-Data/main/capes/cape-links.txt";
    private final Map<String, String> cachedURLs;

    public CapeAPI() {
        this.cachedURLs = new HashMap<>();
    }

    //load cape from cache
    public Identifier getIdentifiedCape(UUID id) {
        for (IdentifiedCape identifiedCape : cachedIdentifiedCapes) {
            if (identifiedCape.capeOwner.id().equals(id.toString())) {
                return identifiedCape.identifier;
            }
        }
        return null;
    }

    //gets URL from URL List
    public String getCapeUrl(String name) {
        Stream<String> lines = Http.get(CAPES_URL).sendLines();
        if (lines != null) {
            lines.forEach(s -> {
                String[] split = s.split(" ");
                cachedURLs.computeIfAbsent(split[0], k -> split[1]);
            });
        }
        return cachedURLs.get(name);
    }

    //cache cape and give it to the TextureManager
    public void register(IdentifiedCape identifiedCape) {
        Identifier id = identifiedCape.identifier;
        NativeImage img = identifiedCape.nativeImage;
        MinecraftClient.getInstance().getTextureManager().registerTexture(id, new NativeImageBackedTexture(img));
        cachedIdentifiedCapes.add(identifiedCape);
    }

    // Does he have a cape
    public void isCapeOwner(UUID id) {
        Stream<String> lines = Http.get(CAPE_OWNERS_URL).sendLines();
        if (lines != null) {
            lines.forEach(s -> {
                String[] split = s.split(" ");
                LOGGER.info("Player found(uuid + capename + ingamename): " + s);
                if (split.length >= 2) {
                    NativeImage img = getImageThroughURL(getCapeUrl(split[1]));
                    register(new IdentifiedCape(img, new CapeOwner(img, id.toString())));
                }
            });
        }
    }

    // decode the Image
    public NativeImage getCapeTexture(CapeRecord cape) {
        try {
            return ImageFromBase64(cape.textureURL());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public CapeRecord getCape(UUID id) {
        //Send JSONDOC with UUID.
        String outImg = "56345i4390oi543";
        return null;
    }

    //get Image based on Webimage
    public NativeImage getImageThroughURL(String URL) {
        InputStream in = Http.get(URL).sendInputStream();
        NativeImage img;
        try {
            img = NativeImage.read(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return img;
    }

    //encode mechanic
    public String ImageToBase64(NativeImage image) throws IllegalStateException {
        try {
            return Base64Coder.encodeLines(image.getBytes());
        } catch (Exception e) {
            throw new IllegalStateException("Image Error", e);
        }
    }

    //Decode mechanic
    public NativeImage ImageFromBase64(String data) throws IOException {
        try {
            return NativeImage.read(Base64Coder.decodeLines(data));
        } catch (Exception e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }
}