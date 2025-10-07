package beame.feature.ui;

import beame.Essence;
import beame.util.BindMapping;
import beame.util.Scissor;
import beame.util.animation.AnimationMath;
import beame.util.color.ColorUtils;
import beame.util.drag.Dragging;
import beame.util.fonts.CustomFont;
import beame.util.fonts.Fonts;
import beame.util.render.ClientHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import beame.module.Module;
import net.minecraft.client.Minecraft;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.awt.*;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;

public class KeybindsDraw {
// leaked by itskekoff; discord.gg/sk3d l4ZfsZrV
    private static final Log log = LogFactory.getLog(KeybindsDraw.class);
    public Dragging KeybindsDrag = Essence.getHandler().createDraggable("keybinds", 90, 95);
    float animatedHbinds = 16;
    float animatedWbinds = 80;
    private float animation = 0;

    public void render() {
        boolean isEnabled = !Essence.getHandler().getModuleList().getModules().stream().filter(module -> module.isState() && module.getBind() > 0).toList().isEmpty();

        float x = KeybindsDrag.getX();
        float y = KeybindsDrag.getY();

        int bindCount = 0;
        int bindCountV = 0;

        for (Module m : Essence.getHandler().getModuleList().getModules()) {
            if (m.isState() && m.getBind() != 0) {
                bindCount += 1;
            }
        }

        boolean hasBinds = bindCount > 0;
        boolean isChatOpen = Minecraft.getInstance().currentScreen instanceof net.minecraft.client.gui.screen.ChatScreen;
        boolean shouldShow = hasBinds || isChatOpen;
        
        animation = AnimationMath.fast(animation, shouldShow ? 1 : 0, 12);
        if (animation == 0) return;

        animatedHbinds = AnimationMath.fast(animatedHbinds, (bindCount * 11) + (bindCount > 0 ? 6 : 0) - 1, 7);
        animatedWbinds = AnimationMath.fast(animatedWbinds, (bindCount > 0 ? 83 : 80), 8);

        RenderSystem.pushMatrix();
        AnimationMath.sizeAnimation(x + (animatedWbinds / 2), y + (16 / 2), animation);

        ClientHandler.drawSexyRect(x, y, animatedWbinds, 16, Essence.getHandler().getModuleList().hud.rounding.get(), true);


        if (hasBinds) {
            ClientHandler.drawSexyRect(x, y + 20, animatedWbinds, animatedHbinds, Essence.getHandler().getModuleList().hud.rounding.get(), false);

            Scissor.push();
            Scissor.setFromComponentCoordinates(x, y + 20, animatedWbinds, animatedHbinds);

            for (Module m : Essence.getHandler().getModuleList().getModules()) {
                String featureName = m.getName();
                int featureKey = m.getBind();
                int alpha = (int)(255 * animation);

                if (m.isState() && m.getBind() != 0) {
                    CustomFont f = Fonts.SUISSEINTL.get(13);
                    String b = BindMapping.getKey(featureKey);
                    Fonts.ESSENCE_ICONS.get(13).drawString(m.getCategory().icon(), x + 4, y + 22 + (bindCountV*11) + 2.5f + 3, ColorUtils.rgba(60, 60, 60, alpha));
                    f.drawString(featureName, x + 14, y + 21.5f + (bindCountV*11) + 2.5f + 3, ColorUtils.rgba(180, 180, 180, alpha));
                    f.drawString(b, x + animatedWbinds - 8 - f.getStringWidth(b), y + 21.5f + (bindCountV*11) + 2.5f + 3, ColorUtils.setAlpha(Essence.getHandler().themeManager.getColor(0), alpha));

                    bindCountV += 1;
                }
            }

            Scissor.unset();
            Scissor.pop();
        }
        int iconColor = ColorUtils.setAlpha(Essence.getHandler().themeManager.getColor(0), (int)(255 * animation));
        Fonts.ESSENCE_ICONS.get(15).drawString("i", x + animatedWbinds - 10 - 5, y + 2 + 5, iconColor);

        int titleColor = ColorUtils.setAlpha(new Color(255, 255, 255, 225).getRGB(), (int)(225 * animation));
        Fonts.SUISSEINTL.get(13).drawString("Keybinds", x + 5, y + 2 + 5, titleColor);

        RenderSystem.popMatrix();

        KeybindsDrag.setWidth(animatedWbinds);
        KeybindsDrag.setHeight(17);
    }
}
