package me.pixel.common;

public class PlayerData {
    private String uuid;
    private boolean cape;
    private boolean ears;
    private boolean flipped;
    private boolean enchantedCape;
    private String capeTexture;
    private String earsTexture;

    public PlayerData(String uuid, boolean cape, boolean ears, boolean flipped, boolean enchantedCape, String capeTexture, String earsTexture) {
        this.uuid = uuid;
        this.cape = cape;
        this.ears = ears;
        this.flipped = flipped;
        this.enchantedCape = enchantedCape;
        this.capeTexture = capeTexture;
        this.earsTexture = earsTexture;
    }

    public String getUuid() {
        return uuid;
    }

    public boolean hasCape() {
        return cape;
    }

    public boolean hasEars() {
        return ears;
    }

    public boolean isFlipped() {
        return flipped;
    }

    public boolean hasEnchantedCape() {
        return enchantedCape;
    }

    public String getCapeTexture() {
        return capeTexture;
    }

    public String getEarsTexture() {
        return earsTexture;
    }
}
