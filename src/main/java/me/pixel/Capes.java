package me.pixel;

import me.pixel.event.CapeEventListener;
import me.pixel.perk.serverconnection;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Capes implements ModInitializer {
    public static final String MOD_ID = "capes";
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        CapeEventListener.register();
        serverconnection serverconnection = new serverconnection();
        serverconnection.onInitializeClient();
        LOGGER.info("Capes Mod initialized");
    }
}