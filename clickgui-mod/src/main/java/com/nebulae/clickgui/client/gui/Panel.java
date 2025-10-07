package com.nebulae.clickgui.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.nebulae.clickgui.client.data.CategoryData;
import com.nebulae.clickgui.util.AnimationUtil;
import com.nebulae.clickgui.util.ColorUtil;
import com.nebulae.clickgui.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class Panel {
    private final CategoryData category;
    private final int index;
    private final float width;
    private final float height;
    private final List<ModuleButton> modules = new ArrayList<>();
    private float x;
    private float y;
    private float scrollOffset;
    private float targetScrollOffset;
    private boolean searching;
    private String searchQuery = "";

    public Panel(CategoryData category, int index, float width, float height) {
        this.category = category;
        this.index = index;
        this.width = width;
        this.height = height;

        for (int i = 0; i < category.getModules().size(); i++) {
            modules.add(new ModuleButton(category.getModules().get(i), width - 12.0F, 20.0F));
        }
    }

    public void tick() {
        Minecraft minecraft = Minecraft.getInstance();
        float gap = 5.0F;
        int panelCount = ClickGuiScreen.PANEL_COUNT;

        float screenWidth = minecraft.getMainWindow().getScaledWidth();
        float screenHeight = minecraft.getMainWindow().getScaledHeight();
        float totalWidth = panelCount * width + (panelCount - 1) * gap;

        this.x = (screenWidth - totalWidth) / 2.0F + index * (width + gap);
        this.y = (screenHeight - height) / 2.0F;

        float contentHeight = modules.stream()
                .filter(button -> matchesSearch(button.getName()))
                .map(ModuleButton::getHeight)
                .reduce(0.0F, (acc, moduleHeight) -> acc + moduleHeight + 2.0F);

        float viewHeight = height - 30.0F;
        float maxScroll = Math.min(0.0F, viewHeight - contentHeight - 8.0F);
        if (contentHeight <= viewHeight) {
            targetScrollOffset = 0.0F;
        } else {
            targetScrollOffset = MathHelper.clamp(targetScrollOffset, maxScroll, 0.0F);
        }
    }

    public void render(MatrixStack stack, double mouseX, double mouseY, float partialTicks) {
        scrollOffset = AnimationUtil.fast(scrollOffset, targetScrollOffset, 12.0F);
        drawBackground(stack);
        drawModules(stack, mouseX, mouseY, partialTicks);
    }

    private void drawBackground(MatrixStack stack) {
        FontRenderer font = Minecraft.getInstance().fontRenderer;
        int background = ColorUtil.rgba(18, 18, 24, 215);
        int accent = ColorUtil.rgba(58, 105, 255, 220);

        RenderUtil.drawRoundedRect(stack, x, y, width, 24.0F, 6.0F, background);
        RenderUtil.drawRoundedRect(stack, x, y + 26.0F, width, height - 26.0F, 6.0F, ColorUtil.rgba(12, 12, 18, 190));

        String header = category.getIcon() + "  " + category.getName();
        float textWidth = font.getStringWidth(header);
        font.drawString(stack, header, x + width / 2.0F - textWidth / 2.0F, y + 8.0F, 0xFFFFFF);

        if (index == 0) {
            font.drawString(stack, "Nebulae", x + 8.0F, y + height + 10.0F, accent);
            font.drawString(stack, "Right Shift - open GUI", x + 8.0F, y + height + 22.0F, 0xAAAAAA);
        } else if (index == 2) {
            font.drawString(stack, "LShift - show keybind overlay", x + 6.0F, y + height + 10.0F, 0xAAAAAA);
            font.drawString(stack, "CTRL+F - search modules", x + 6.0F, y + height + 22.0F, 0xAAAAAA);
        } else if (index == 3) {
            float fieldX = x + width - 98.0F;
            float fieldY = y + height + 8.0F;
            RenderUtil.drawRoundedRect(stack, fieldX, fieldY, 92.0F, 18.0F, 4.0F, ColorUtil.rgba(24, 24, 32, 200));
            String placeholder = searching ? searchQuery + (System.currentTimeMillis() % 1000 > 500 ? "_" : "") : searchQuery.isEmpty() ? "Search..." : searchQuery;
            int color = searchQuery.isEmpty() && !searching ? 0x555555 : 0xFFFFFF;
            font.drawString(stack, placeholder, fieldX + 6.0F, fieldY + 5.0F, color);
        }
    }

    private void drawModules(MatrixStack stack, double mouseX, double mouseY, float partialTicks) {
        float moduleX = x + 6.0F;
        float moduleY = y + 32.0F + scrollOffset;
        float clipTop = y + 26.0F;
        float clipBottom = clipTop + height - 32.0F;

        for (ModuleButton button : modules) {
            if (!matchesSearch(button.getName())) {
                continue;
            }
            if (moduleY + button.getHeight() < clipTop) {
                moduleY += button.getHeight() + 2.0F;
                continue;
            }
            if (moduleY > clipBottom) {
                break;
            }
            button.setPosition(moduleX, moduleY);
            button.render(stack, mouseX, mouseY, partialTicks);
            moduleY += button.getHeight() + 2.0F;
        }
    }

    private boolean matchesSearch(String name) {
        return searchQuery.isEmpty() || name.toLowerCase().contains(searchQuery.toLowerCase());
    }

    public void mouseScrolled(double mouseX, double mouseY, double delta) {
        if (!isInside(mouseX, mouseY)) {
            return;
        }
        float amount = (float) (delta * 12.0F);
        targetScrollOffset = MathHelper.clamp(targetScrollOffset + amount, -1000.0F, 0.0F);
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (index == 3) {
            float fieldX = x + width - 98.0F;
            float fieldY = y + height + 8.0F;
            if (mouseX >= fieldX && mouseX <= fieldX + 92.0F && mouseY >= fieldY && mouseY <= fieldY + 18.0F) {
                searching = true;
                return;
            }
        }
        searching = false;
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
        // No module interactions required for this showcase GUI.
    }

    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        if (searching) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                searching = false;
            }
            if (keyCode == GLFW.GLFW_KEY_BACKSPACE && !searchQuery.isEmpty()) {
                searchQuery = searchQuery.substring(0, searchQuery.length() - 1);
            }
            if (keyCode == GLFW.GLFW_KEY_ENTER) {
                searching = false;
            }
        } else if (keyCode == GLFW.GLFW_KEY_F && (modifiers & GLFW.GLFW_MOD_CONTROL) != 0) {
            searching = true;
        }
    }

    public boolean charTyped(char codePoint, int modifiers) {
        if (!searching) {
            return false;
        }
        if (Character.isISOControl(codePoint)) {
            return false;
        }
        if (searchQuery.length() >= 20) {
            return false;
        }
        searchQuery += codePoint;
        return true;
    }

    public boolean isInside(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public ModuleButton getHoveredModule(double mouseX, double mouseY) {
        for (ModuleButton moduleButton : modules) {
            if (!matchesSearch(moduleButton.getName())) {
                continue;
            }
            if (moduleButton.isHovered(mouseX, mouseY)) {
                return moduleButton;
            }
        }
        return null;
    }
}
