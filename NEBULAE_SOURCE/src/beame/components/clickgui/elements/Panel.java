package beame.components.clickgui.elements;

import beame.Nebulae;
import beame.util.Scissor;
import beame.util.animation.AnimationMath;
import beame.util.color.ColorUtils;
import beame.util.fonts.Fonts;
import beame.util.render.ClientHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import beame.module.Category;
import beame.module.IRenderable;
import beame.module.Module;
import beame.module.ModuleComponent;
import org.lwjgl.glfw.GLFW;
import java.awt.*;
import java.util.ArrayList;

import static beame.util.IMinecraft.mc;
import static beame.util.color.ColorUtils.interpolateColor;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

public class Panel implements IRenderable {
// leaked by itskekoff; discord.gg/sk3d 8YHkejCQ
    public float x = 0;
    public float y = 0;
    public float width = 0;
    public float height = 0;

    public int index = 0;
    private Category category = Category.Combat;

    public float scrollTemp;
    private float scroll;

    public boolean searching;
    private String searchText = "";

    public ArrayList<ModuleComponent> moduleComponents = new ArrayList<>();

    public Panel(Category category, int index, float width, float height) {
        this.category = category;
        this.index = index;
        this.width = width;
        this.height = height;

        for (Module module : Nebulae.getHandler().getModuleList().getModules()) {
            if(module.getCategory() != category) continue;
            if(!module.isVisible()) continue;
            moduleComponents.add(new ModuleComponent(module));
        }
    }

    public void tick() {
        float gap = 5f;
        int panelCount = Category.values().length;

        float screenWidth = (float) mc.getMainWindow().getScaledWidth();
        float screenHeight = (float) mc.getMainWindow().getScaledHeight();

        float totalWidth = panelCount * width + (panelCount - 1) * gap;

        x = (screenWidth - totalWidth) / 2f + index * (width + gap);
        y = (screenHeight - height) / 2f;

        float totalModuleHeight = 0;
        for (ModuleComponent component : moduleComponents) {
            totalModuleHeight += component.getHeight() + 2;
        }

        float visibleHeight = height - 25;
        if (totalModuleHeight > visibleHeight) {
            float maxScroll = -(totalModuleHeight - visibleHeight);
            scrollTemp = Math.min(0, Math.max(scrollTemp, maxScroll));
        } else {
            scrollTemp = 0;
        }
    }


    public void draw(MatrixStack stack, float mouseX, float mouseY) {
        scroll = AnimationMath.fast(scroll, scrollTemp, 8);

        drawBackground(stack, mouseX, mouseY);
        drawModules(stack, mouseX, mouseY);
    }


    public void drawBackground(MatrixStack stack, float mouseX, float mouseY) {
        String username = Nebulae.getHandler().getUserName();
        ClientHandler.drawSexyRectFromPanel(x, y, width, 20, 6, false);
        ClientHandler.drawSexyRectFromPanel(x, y + 20 + 5, width, height - 15f, 6, false);

        float size0 = Fonts.SUISSEINTL.get(14).getStringWidth(category.name());

        Fonts.ESSENCE_ICONS.get(19).drawString(category.icon().toLowerCase(), x + (width / 2) - (size0 / 2) - 15, y + 19f / 2 - 1f, Nebulae.getHandler().themeManager.getColor(0));
        Fonts.SUISSEINTL.get(16).drawString(category.name(), x + (width / 2) - (size0 / 2) - 1, y + 20f / 2 - 1.5f, -1);
        if(index == 0) {

            String uid = Nebulae.getHandler().getUserID() + "";
            float uidSize = Fonts.SUISSEINTL.get(14).getWidth(uid) + 1.5f;

            float width2 = width;
            float x2 = x + width2 - 15f;
            float y2 = y + height + 10 + 5;

            float x3 = x + width2 - 30f;

            ClientHandler.drawSexyRectFromPanel(x, y2, width2, 20, 6, false);

            int bg_0 = interpolateColor(interpolateColor(Nebulae.getHandler().themeManager.getColor(0), 3, 0.7f), ColorUtils.rgba(45, 45, 45, 80), 50);
            int bg01 = ColorUtils.rgba(45, 45, 45, 80);
            int bg_1 = interpolateColor(interpolateColor(Nebulae.getHandler().themeManager.getColor(180), 3, 0.5f), bg01, 50);


            float roundWidth = uidSize + 5;

            float marginRight = 5;

            float roundX = x + width2 - roundWidth - marginRight;

            ClientHandler.drawRound(roundX, y2 + 5.0f, roundWidth, 10, 2, Nebulae.getHandler().getThemeManager().getColor(0));
            Fonts.SUISSEINTL.get(14).drawString(uid, roundX + 2.5f, y2 + 8.5f, ColorUtils.rgba(0, 0, 0, 255));

            Fonts.ESSENCE_ICONS.get(17).drawString("b", x2 - 80f, y2 + 8.5f, Nebulae.getHandler().themeManager.getColor(0));
            Fonts.SUISSEINTL.get(14).drawString(username.length() > 12 ? username.substring(0, 12) : username, x2 - 67.5f, y2 + 8.5f, -1);

        } else if (index == 3) {
            float width2 = width;
            float x2 = x + 105.5f;
            float y2 = y + height + 10 + 5;

            ClientHandler.drawSexyRectFromPanel(x + 105, y + height + 15, width2, 20, 6, false);

            Scissor.push();
            Scissor.setFromComponentCoordinates(x + 105, y2, width2, 20);
            if (!searching && searchText.isEmpty()) {
                Fonts.ESSENCE_ICONS.get(17).drawString("g", x2 + 82.5f, y2 + 9f, new Color(60, 60, 60, 255).getRGB());
                Fonts.SUISSEINTL.get(14).drawString(stack, "Поиск модулей...", x2 + 5f, y2 + 8.5f, new Color(60, 60, 60, 255).getRGB());
            } else {
                Fonts.SUISSEINTL.get(14).drawString(stack, searchText + (searching ? (System.currentTimeMillis() % 1000 > 500 ? "_" : "") : ""), x2 + 5f, y2 + 8.5f, Nebulae.getHandler().getThemeManager().getColor(0));
            }
            Scissor.unset();
            Scissor.pop();

        } else if(index == 2) {
            Fonts.SUISSEINTL.get(12).drawCenteredString("LShift - для просмотра биндов.", x + width / 2, y + height + 5 + 15f / 2 - 1.5f + 10, new Color(255, 255, 255, 150).getRGB());
            Fonts.SUISSEINTL.get(12).drawCenteredString("CTRL + F - поиск по модулям.", x + width / 2, y + height + 5 + 30f / 2 - 1.5f + 10, new Color(255, 255, 255, 150).getRGB());
            Fonts.SUISSEINTL.get(12).drawCenteredString("CTRL + V - вставить текст в поле ввода.", x + width / 2, y + height + 5 + 45f / 2 - 1.5f + 10, new Color(255, 255, 255, 150).getRGB());
        }
    }

    public void drawModules(MatrixStack stack, float mouseX, float mouseY) {
        float totalModuleHeight = 0;
        for (ModuleComponent module : moduleComponents) {
            totalModuleHeight += module.getHeight() + 2;
        }

        float visibleHeight = height - 25;
        if (totalModuleHeight <= visibleHeight) {
            scroll = 0;
        } else {
            scroll = AnimationMath.fast(scroll, scrollTemp, 8);
            float maxScroll = -(totalModuleHeight - visibleHeight);
            scroll = Math.min(0, Math.max(scroll, maxScroll));
        }

        Scissor.push();
        Scissor.setFromComponentCoordinates(x, y + 25, width, height - 15);

        float moduleX = x + 5;
        float moduleY = y + 25 + 5 + scroll;
        float clipTop = y + 25;
        float clipBottom = clipTop + (height - 15);

        for (ModuleComponent module : moduleComponents) {
            if (!searchText.isEmpty() && !module.getModule().getName().toLowerCase().contains(searchText.toLowerCase())) {
                continue;
            }

            float moduleBottom = moduleY + module.getHeight();
            if (moduleBottom < clipTop || moduleY > clipBottom) {
                moduleY += module.getHeight() + 2;
                continue;
            }

            module.setX(moduleX);
            module.setY(moduleY);
            module.render(stack, mouseX, mouseY);
            moduleY += module.getHeight() + 2;
        }


        Scissor.unset();
        Scissor.pop();
    }


    @Override
    public void charTyped(char codePoint, int modifiers) {
        if (searching && searchText.length() < 13) {
            searchText += codePoint;
        }
        for (ModuleComponent component : moduleComponents) {
            component.charTyped(codePoint, modifiers);
        }
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
        for (ModuleComponent module : moduleComponents) {

            if (!searchText.isEmpty() && !module.getModule().getName().toLowerCase().contains(searchText.toLowerCase())) {
                continue;
            }
            module.mouseReleased((int) mouseX, (int) mouseY, button);
        }
    }
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        for (ModuleComponent component : moduleComponents) {
            component.keyPressed(keyCode, scanCode, modifiers);
        }
        if (searching) {
            if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
                if (!searchText.isEmpty())
                    searchText = searchText.substring(0, searchText.length() - 1);
            }
            if (keyCode == GLFW.GLFW_KEY_ENTER) {
                searching = false;
            }
        }
        if (keyCode == GLFW.GLFW_KEY_F && GLFW.glfwGetKey(mc.getMainWindow().getHandle(), GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW_PRESS) {
            searching = true;
        }

    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        float mX = (int) mouseX;
        float mY = (int) mouseY;

        if (ClientHandler.isInRegion((int) mouseX, (int) mouseY, (int) x, (int) y, (int) width, (int) height)) {
            for (ModuleComponent module : moduleComponents) {
                if (!searchText.isEmpty() && !module.getModule().getName().toLowerCase().contains(searchText.toLowerCase())) {
                    continue;
                }
                module.mouseClicked(mX, mY, button);
            }
        }
    }
}
