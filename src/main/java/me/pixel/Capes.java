package me.pixel;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Capes implements ModInitializer {
	public static final String MOD_ID = "capes";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		me.pixel.meteor.MeteorExecutor.init();
		me.pixel.perk.Capes.init();
		ClientTickEvents.END_CLIENT_TICK.register(me.pixel.perk.Capes::onTick);
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			dispatcher.register(ClientCommandManager.literal("reloadcapes").executes(context -> {
				me.pixel.perk.newcapes.init();
				context.getSource().sendFeedback(Text.literal("Reloaded capes").formatted(Formatting.GREEN));
				return 1;
			}));
		});
	}
}