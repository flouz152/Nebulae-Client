package com.nebulae.clickgui.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.AbstractGui;

public final class RenderUtil {
    private RenderUtil() {
    }

    public static void drawRoundedRect(MatrixStack stack, float x, float y, float width, float height, float radius, int color) {
        // For simplicity approximate rounded rectangle using four rectangles and no arcs.
        // The panels are relatively large, so this approximation works well for a shader-free implementation.
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();

        float r = AnimationUtil.clamp(radius, 0.0F, Math.min(width, height) / 2.0F);
        AbstractGui.fill(stack, (int) (x + r), (int) y, (int) (x + width - r), (int) (y + height), color);
        AbstractGui.fill(stack, (int) x, (int) (y + r), (int) (x + r), (int) (y + height - r), color);
        AbstractGui.fill(stack, (int) (x + width - r), (int) (y + r), (int) (x + width), (int) (y + height - r), color);

        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public static void drawGradientRect(MatrixStack stack, float x, float y, float width, float height, int startColor, int endColor) {
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();

        AbstractGui.fill(stack, (int) x, (int) y, (int) (x + width), (int) (y + height / 2f), startColor);
        AbstractGui.fill(stack, (int) x, (int) (y + height / 2f), (int) (x + width), (int) (y + height), endColor);

        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }
}
