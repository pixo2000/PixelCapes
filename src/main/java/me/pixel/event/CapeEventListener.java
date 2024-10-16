package me.pixel.event;

import me.pixel.perk.CapeAPI;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;

public class CapeEventListener {
    public static void register() {
        ClientLoginConnectionEvents.INIT.register((handler, client) -> {
            CapeAPI.INSTANCE.loadCapes();
        });
    }
}