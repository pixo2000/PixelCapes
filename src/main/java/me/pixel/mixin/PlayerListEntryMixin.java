package me.pixel.mixin;

import com.mojang.authlib.GameProfile;
import me.pixel.perk.CapeAPI;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Mixin(value = PlayerListEntry.class, priority = 100)
public abstract class PlayerListEntryMixin {

    @Final
    @Shadow
    private Supplier<SkinTextures> texturesSupplier;

    @Final
    @Shadow
    private GameProfile profile;

    @Inject(method = "getSkinTextures", at = @At("RETURN"), cancellable = true)
    private void getSkinTexture(CallbackInfoReturnable<SkinTextures> cir) {
        try {
            CapeAPI capeAPI = CapeAPI.INSTANCE;
            if (capeAPI.isCapeOwner(profile.getId())) {
                Identifier customCape = capeAPI.getIdentifiedCape(profile.getId());
                if (customCape != null) {
                    SkinTextures originalTextures = cir.getReturnValue();
                    cir.setReturnValue(
                            new SkinTextures(
                                    originalTextures.texture(),
                                    originalTextures.textureUrl(),
                                    customCape,
                                    originalTextures.elytraTexture(),
                                    originalTextures.model(),
                                    originalTextures.secure()
                            )
                    );
                }
            }
        } catch (Exception e) {
            // Log the error or handle it appropriately
            System.err.println("Error applying custom cape: " + e.getMessage());
        }
    }
}
