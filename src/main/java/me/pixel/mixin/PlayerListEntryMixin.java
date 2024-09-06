package me.pixel.mixin;

import com.mojang.authlib.GameProfile;
import me.pixel.perk.Capes;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

@Mixin(value = PlayerListEntry.class, priority = 1500)
public abstract class PlayerListEntryMixin {

    @Final
    @Shadow
    private Supplier<SkinTextures> texturesSupplier;

    @Final
    @Shadow
    private GameProfile profile;

    @Inject(method = "getSkinTextures", at = @At("HEAD"), cancellable = true)
    private void getSkinTexture(CallbackInfoReturnable<SkinTextures> cir) {
        Identifier cape = Capes.get(profile.getId());
        Identifier capeId = cape != null ? cape : texturesSupplier.get().capeTexture();
        Identifier skinId = texturesSupplier.get().texture();
        SkinTextures.Model model = texturesSupplier.get().model();

        if(cape != null) {
            cir.setReturnValue(
                new SkinTextures(
                    skinId,
                    texturesSupplier.get().textureUrl(),
                    capeId,
                    texturesSupplier.get().elytraTexture(),
                    model,
                    texturesSupplier.get().secure()
                )
            );
        }
    }

}