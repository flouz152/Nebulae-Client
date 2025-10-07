package beame.components.clickgui;

import beame.Essence;
import beame.components.clickgui.elements.Panel;
import beame.util.animation.AnimationMath;
import beame.util.color.ColorUtils;
import beame.util.fonts.Fonts;
import beame.util.render.ClientHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import beame.module.Category;
import beame.module.ModuleComponent;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import static beame.util.IMinecraft.mc;
import static beame.util.color.ColorUtils.interpolateColor;

public class DropDownGui extends Screen {
// leaked by itskekoff; discord.gg/sk3d C218J6Uc
    public List<Panel> panels = new ArrayList<>();


    public float alpha = 0;
    public float needAlpha = 1;
    public boolean close = false;

    public float firstAnimationDescription;
    public float hoverAnimationDescription = 0;

    public DropDownGui(ITextComponent titleIn) {
        super(titleIn);

        float width = 100;
        float height = 270;
        int index = 0;
        for(Category cat : Category.values()) {
            panels.add(new Panel(cat, index, width, height));
            index+=1;
        }
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if(!close) needAlpha = 1;

        if (Essence.getHandler().getModuleList().getClickGUI().clickGuiHints.get()) {
            boolean anyHovered = false;
            for (Panel panel : panels) {
                for (ModuleComponent module : panel.moduleComponents) {
                    if (module.isHovered() && module.getModule().getDescription() != null) {
                        anyHovered = true;

                        firstAnimationDescription = AnimationMath.fast(firstAnimationDescription, 1, 2);
                        hoverAnimationDescription = AnimationMath.lerp(hoverAnimationDescription, 1, 2);

                        int colorWhite = ColorUtils.rgba(255, 255, 255, 255);

                        int endColor = interpolateColor(-1, colorWhite, firstAnimationDescription);
                        int startColor = new Color(255, 255, 255, 0).getRGB();
                        int animatedColor = interpolateColor(endColor, startColor, hoverAnimationDescription);

                        String description = module.getModule().getDescription();
                        float textWidth = Fonts.SUISSEINTL.get(20).getStringWidth(description);
                        float xPosition = (float) mc.getMainWindow().getScaledWidth() / 2 - textWidth / 2;
                        float yPosition = (float) mc.getMainWindow().getScaledHeight() / 2 - 165;

                        Fonts.SUISSEINTL.get(20).drawString(description, xPosition, yPosition, animatedColor);
                    }
                }
            }

            if (!anyHovered) {
                firstAnimationDescription = AnimationMath.fast(firstAnimationDescription, 0, 2);
                hoverAnimationDescription = AnimationMath.lerp(hoverAnimationDescription, 0, 2);
            }
        }

        mc.gameRenderer.setupOverlayRendering(2);

        RenderSystem.pushMatrix();
        AnimationMath.sizeAnimation((double) mc.getMainWindow().getScaledWidth() / 2, (double) mc.getMainWindow().getScaledHeight() / 2, alpha);
        for (Panel panel : panels) {
            panel.tick();
            panel.draw(matrixStack, mouseX, mouseY);
        }

        RenderSystem.popMatrix();

        mc.gameRenderer.setupOverlayRendering();

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        for(Panel panel : panels) {
            panel.charTyped(codePoint, modifiers);
        }

        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for(Panel panel : panels) {
            panel.keyPressed(keyCode, scanCode, modifiers);
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for(Panel panel : panels) {
            if (ClientHandler.isInRegion((int) mouseX, (int) mouseY, (int) (panel.x), (int) (panel.y + 25), (int) (panel.width), (int) (panel.height - 25))) {
                panel.mouseClicked(mouseX, mouseY, button);
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for(Panel panel : panels) {
            panel.mouseReleased(mouseX, mouseY, button);
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        for(Panel panel : panels) {
            if (ClientHandler.isInRegion((int) mouseX, (int) mouseY, (int) (panel.x), (int) (panel.y + 25), (int) (panel.width), (int) (panel.height - 25))) {
                panel.scrollTemp += (float) (delta * 10);

                float totalModuleHeight = 0;
                for (ModuleComponent module : panel.moduleComponents) {
                    totalModuleHeight += module.getHeight() + 2;
                }
                panel.scrollTemp = MathHelper.clamp(panel.scrollTemp, -(totalModuleHeight), 0);
            }
        }

        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public void closeScreen() {
        close = true;
        needAlpha = 0;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
