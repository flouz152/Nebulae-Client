package com.nebulae.clickgui.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.nebulae.clickgui.client.data.ModuleData;
import com.nebulae.clickgui.util.AnimationUtil;
import com.nebulae.clickgui.util.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class ModuleButton {
    private final ModuleData data;
    private float x;
    private float y;
    private final float width;
    private final float height;
    private float hoverProgress;

    public ModuleButton(ModuleData data, float width, float height) {
        this.data = data;
        this.width = width;
        this.height = height;
    }

    public void render(MatrixStack stack, double mouseX, double mouseY, float partialTicks) {
        boolean hovered = isHovered(mouseX, mouseY);
        hoverProgress = AnimationUtil.fast(hoverProgress, hovered ? 1.0F : 0.0F, 12.0F);

        int baseColor = ColorUtil.rgba(34, 34, 42, 200);
        int hoverColor = ColorUtil.rgba(58, 105, 255, 200);
        int fill = ColorUtil.lerp(baseColor, hoverColor, hoverProgress * 0.35F);

        net.minecraft.client.gui.AbstractGui.fill(stack, (int) x, (int) y, (int) (x + width), (int) (y + height), fill);

        FontRenderer font = Minecraft.getInstance().fontRenderer;
        float textY = y + (height - font.FONT_HEIGHT) / 2.0F;
        font.drawString(stack, data.getName(), x + 6.0F, textY, 0xFFFFFF);
    }

    public boolean isHovered(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public String getDescription() {
        return data.getDescription();
    }

    public String getName() {
        return data.getName();
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getHeight() {
        return height;
    }
}
