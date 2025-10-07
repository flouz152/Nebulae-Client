package com.nebulae.clickgui.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.nebulae.clickgui.client.data.CategoryData;
import com.nebulae.clickgui.client.data.ClickGuiData;
import com.nebulae.clickgui.util.AnimationUtil;
import com.nebulae.clickgui.util.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;
import java.util.List;

public class ClickGuiScreen extends Screen {
    public static final int PANEL_COUNT = ClickGuiData.createDefaultCategories().size();

    private final List<Panel> panels = new ArrayList<>();
    private float alpha = 0.0F;
    private float targetAlpha = 1.0F;
    private boolean closing = false;

    public ClickGuiScreen() {
        super(new StringTextComponent("Nebulae Click GUI"));
        List<CategoryData> categories = ClickGuiData.createDefaultCategories();
        for (int i = 0; i < categories.size(); i++) {
            panels.add(new Panel(categories.get(i), i, 130.0F, 260.0F));
        }
    }

    @Override
    public void tick() {
        panels.forEach(Panel::tick);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);

        alpha = AnimationUtil.fast(alpha, targetAlpha, 18.0F);
        float scale = 0.85F + 0.15F * alpha;

        double centerX = this.width / 2.0D;
        double centerY = this.height / 2.0D;

        matrixStack.push();
        matrixStack.translate(centerX, centerY, 0);
        matrixStack.scale(scale, scale, scale);
        matrixStack.translate(-centerX, -centerY, 0);

        for (Panel panel : panels) {
            panel.render(matrixStack, mouseX, mouseY, partialTicks);
        }

        matrixStack.pop();

        ModuleButton hovered = getHoveredModule(mouseX, mouseY);
        if (hovered != null && hovered.getDescription() != null && !hovered.getDescription().isEmpty()) {
            drawDescription(matrixStack, hovered.getDescription());
        }

        if (closing && Math.abs(alpha - targetAlpha) < 0.02F) {
            Minecraft.getInstance().displayGuiScreen(null);
        }
    }

    private void drawDescription(MatrixStack stack, String description) {
        Minecraft minecraft = Minecraft.getInstance();
        int screenWidth = minecraft.getMainWindow().getScaledWidth();
        int screenHeight = minecraft.getMainWindow().getScaledHeight();
        int textWidth = minecraft.fontRenderer.getStringWidth(description);
        int boxWidth = textWidth + 20;
        int boxHeight = 20;
        int x = screenWidth / 2 - boxWidth / 2;
        int y = screenHeight / 2 - 150;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        AbstractGui.fill(stack, x, y, x + boxWidth, y + boxHeight, ColorUtil.rgba(0, 0, 0, 140));
        minecraft.fontRenderer.drawString(stack, description, x + 10, y + (boxHeight - minecraft.fontRenderer.FONT_HEIGHT) / 2.0F, 0xFFFFFF);
        RenderSystem.disableBlend();
    }

    private ModuleButton getHoveredModule(double mouseX, double mouseY) {
        for (Panel panel : panels) {
            ModuleButton hovered = panel.getHoveredModule(mouseX, mouseY);
            if (hovered != null) {
                return hovered;
            }
        }
        return null;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) { // Escape
            closeAnimated();
            return true;
        }
        for (Panel panel : panels) {
            panel.keyPressed(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        boolean handled = false;
        for (Panel panel : panels) {
            handled |= panel.charTyped(codePoint, modifiers);
        }
        return handled || super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (Panel panel : panels) {
            panel.mouseClicked(mouseX, mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (Panel panel : panels) {
            panel.mouseReleased(mouseX, mouseY, button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        for (Panel panel : panels) {
            panel.mouseScrolled(mouseX, mouseY, delta);
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    private void closeAnimated() {
        if (!closing) {
            closing = true;
            targetAlpha = 0.0F;
        }
    }

    @Override
    public void onClose() {
        closeAnimated();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
