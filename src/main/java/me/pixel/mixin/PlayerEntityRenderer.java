package me.pixel.mixin;

import me.pixel.perk.capeManagement;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(PlayerEntityRenderer.class)
public class PlayerRendererMixin {
    @Inject(at = @At("HEAD"), method = "render")
    private void onRender(PlayerEntity player, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        // Prüfen, ob der Spieler schon in der Liste ist
        UUID playerUUID = player.getUuid();
        if (!capeManagement.checkedPlayers.contains(playerUUID)) {
            // Spieler wurde noch nicht geprüft, Cape zuweisen
            capeManagement.checkPlayer(player);
        }
    }
}