package me.pixel.event;

import me.pixel.perk.CapeAPI;
import net.fabricmc.fabric.api.client.networking.v1.ClientConnectionEvents;

public class CapeEventListener {
    public static void register() {
        ClientConnectionEvents.INIT.register((handler, client) -> {
            CapeAPI.INSTANCE.loadCapes();
        });
    }
}