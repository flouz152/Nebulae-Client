package mdk.by.ghostbitbox.util;

import java.io.IOException;
import java.io.InputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

public final class TargetEspTextures {

    private static final String NAMESPACE = "targetesp";

    private static ResourceLocation glowTexture;
    private static ResourceLocation squareTexture;
    private static ResourceLocation newSquareTexture;

    private TargetEspTextures() {
    }

    public static ResourceLocation getGlowTexture() {
        if (glowTexture == null) {
            glowTexture = locate("glow", "night/image/glow.png");
        }
        return glowTexture;
    }

    public static ResourceLocation getSquareTexture() {
        if (squareTexture == null) {
            squareTexture = locate("square", "night/image/target/Quad.png");
        }
        return squareTexture;
    }

    public static ResourceLocation getNewSquareTexture() {
        if (newSquareTexture == null) {
            newSquareTexture = locate("square_new", "night/image/target/Quad2.png");
        }
        return newSquareTexture;
    }

    private static ResourceLocation locate(String identifier, String relativePath) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null) {
            return new ResourceLocation(relativePath);
        }

        TextureManager textureManager = minecraft.getTextureManager();
        if (textureManager == null) {
            return new ResourceLocation(relativePath);
        }

        ResourceLocation dynamicLocation = new ResourceLocation(NAMESPACE, identifier);
        String normalizedPath = relativePath.startsWith("/") ? relativePath.substring(1) : relativePath;

        for (String candidate : new String[] {
                "assets/minecraft/" + normalizedPath,
                normalizedPath
        }) {
            try (InputStream stream = TargetEspTextures.class.getClassLoader().getResourceAsStream(candidate)) {
                if (stream == null) {
                    continue;
                }

                NativeImage image = NativeImage.read(stream);
                DynamicTexture texture = new DynamicTexture(image);
                textureManager.loadTexture(dynamicLocation, texture);
                return dynamicLocation;
            } catch (IOException exception) {
                System.err.println("[TargetESP] Failed to load texture '" + candidate + "': " + exception.getMessage());
            }
        }

        return new ResourceLocation(relativePath);
    }
}
