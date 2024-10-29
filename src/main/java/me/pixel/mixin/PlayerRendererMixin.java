package me.pixel.mixin;

import me.pixel.client.CapeRenderer;
import me.pixel.client.EarRenderer;
import me.pixel.common.ModMain;
import me.pixel.common.PlayerData;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public class PlayerRendererMixin {

    @Inject(method = "render", at = @At("HEAD"))
    private void renderPlayer(PlayerEntity player, float f, float g, MatrixStack matrixStack, CallbackInfo ci) {
        PlayerData data = ModMain.getPlayerDataFetcher().getPlayerData(player.getUuid().toString());

        if (data != null) {
            if (data.isFlipped()) {
                matrixStack.translate(0, player.getHeight(), 0);
                matrixStack.scale(1.0F, -1.0F, 1.0F);
            }

            if (data.hasCape()) {
                CapeRenderer.renderCape(player, matrixStack, data.getCapeTexture(), data.hasEnchantedCape());
            }

            if (data.hasEars()) {
                EarRenderer.renderEars(player, matrixStack, data.getEarsTexture());
            }
        }
    }
}
