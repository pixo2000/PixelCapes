package me.pixel.mixin;

import net.minecraft.client.network.PlayerListEntry;
import org.spongepowered.asm.mixin.Mixin;


@Mixin(value = PlayerListEntry.class, priority = 100)
public abstract class PlayerListEntryMixin {

    // mixin
}