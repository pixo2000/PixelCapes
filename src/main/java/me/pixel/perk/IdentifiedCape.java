package me.pixel.perk;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;

public  class IdentifiedCape {
    protected Identifier identifier;
    protected NativeImage nativeImage;
    protected CapeOwner capeOwner;

    public IdentifiedCape(NativeImage nativeImage, CapeOwner capeOwner) {
        this.nativeImage = nativeImage;
        this.capeOwner = capeOwner;
        this.identifier = identifier;
        identifier = Identifier.of("capes/"+capeOwner.id());
    }
}
