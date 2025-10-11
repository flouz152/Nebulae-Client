package mdk.by.ghostbitbox.util;

import net.minecraft.util.ResourceLocation;

public final class TargetEspTextures {

    private static final ResourceLocation GLOW = new ResourceLocation("night/image/glow.png");
    private static final ResourceLocation SQUARE = new ResourceLocation("night/image/target/Quad.png");
    private static final ResourceLocation NEW_SQUARE = new ResourceLocation("night/image/target/Quad2.png");

    private TargetEspTextures() {
    }

    public static ResourceLocation getGlowTexture() {
        return GLOW;
    }

    public static ResourceLocation getSquareTexture() {
        return SQUARE;
    }

    public static ResourceLocation getNewSquareTexture() {
        return NEW_SQUARE;
    }
}
