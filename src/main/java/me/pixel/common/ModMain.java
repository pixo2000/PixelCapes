package me.pixel.common;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class ModMain implements ModInitializer {

    public static final String MOD_ID = "beispielmod";
    private static PlayerDataFetcher playerDataFetcher;

    @Override
    public void onInitialize() {
        // Register server start event
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            playerDataFetcher = new PlayerDataFetcher("http://yourserver.com/playerdata");
            playerDataFetcher.fetchPlayerData();
        });

        // Register config options
        ModConfig.loadConfig();
    }

    public static PlayerDataFetcher getPlayerDataFetcher() {
        return playerDataFetcher;
    }
}
