package me.pixel.perk;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import org.lwjgl.system.Configuration;

public class PreLaunch implements PreLaunchEntrypoint {
    @Override
    public void onPreLaunch() {
        System.setProperty("org.lwjgl.system.stackSize", "1024");
        Configuration.STACK_SIZE.set(1024);
        //Addon.LOG.info("Changed stackSize to 512");
    }
}
