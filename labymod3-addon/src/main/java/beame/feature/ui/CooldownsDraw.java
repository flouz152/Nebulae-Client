package beame.feature.ui;

import beame.Essence;
import beame.components.modules.render.ESP;
import beame.util.animation.AnimationMath;
import beame.util.color.ColorUtils;
import beame.util.drag.Dragging;
import beame.util.fonts.CustomFont;
import beame.util.fonts.Fonts;
import beame.util.render.ClientHandler;
import events.impl.render.Render2DEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.CooldownTracker;
import com.mojang.blaze3d.systems.RenderSystem;

import java.awt.*;
import java.util.Iterator;
import java.util.Map;

import static beame.util.IMinecraft.mc;

public class CooldownsDraw {
// leaked by itskekoff; discord.gg/sk3d FPnWBfWy
    public Dragging CooldownsDrag = Essence.getHandler().createDraggable("cooldowns", mc.getMainWindow().getScaledWidth() - 310, 10);

    float animatedHcooldowns;
    private float animation = 0;

    public void render(Render2DEvent render) {
        float x = CooldownsDrag.getX();
        float y = CooldownsDrag.getY();

        CooldownTracker tracker = mc.player.getCooldownTracker();
        Map<Item, CooldownTracker.Cooldown> cooldowns = tracker.cooldowns;


        boolean hasCooldowns = !cooldowns.isEmpty();
        boolean isChatOpen = mc.currentScreen instanceof net.minecraft.client.gui.screen.ChatScreen;
        boolean shouldShow = hasCooldowns || isChatOpen;

        animation = AnimationMath.fast(animation, shouldShow ? 1 : 0, 12);
        if (animation == 0) return;

        animatedHcooldowns = AnimationMath.fast(animatedHcooldowns, (cooldowns.size() * 11) + (!cooldowns.isEmpty() ? 6 : 0) - 1, 4);

        RenderSystem.pushMatrix();
        AnimationMath.sizeAnimation(x + (hasCooldowns ? 100 : 80) / 2, y + (16 / 2), animation);

        ClientHandler.drawSexyRect(x, y, hasCooldowns ? 100 : 80, 16, Essence.getHandler().getModuleList().hud.rounding.get(), true);

        int iconColor = ColorUtils.setAlpha(Essence.getHandler().themeManager.getColor(0), (int)(255 * animation));
        Fonts.ESSENCE_ICONS.get(15).drawString("k", x + (hasCooldowns ? 100 : 80) - 10 - 5, y + 2 + 5, iconColor);

        int titleColor = ColorUtils.setAlpha(new Color(255, 255, 255, 225).getRGB(), (int)(225 * animation));
        Fonts.SUISSEINTL.get(13).drawString("Cooldowns", x + 5, y + 2 + 5, titleColor);

        if (hasCooldowns) {
            ClientHandler.drawSexyRect(x, y + 20, 100, animatedHcooldowns, Essence.getHandler().getModuleList().hud.rounding.get(), false);

            int index = 0;
            int alpha = (int)(255 * animation);

            Iterator<Map.Entry<Item, CooldownTracker.Cooldown>> iterator = cooldowns.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry<Item, CooldownTracker.Cooldown> entry = iterator.next();
                CustomFont f = Fonts.SUISSEINTL.get(13);

                long currentTick = mc.player.getCooldownTracker().getTicks();
                long expireTick = entry.getValue().getExpireTicks();
                long remainingTicks = expireTick - currentTick;

                if (remainingTicks <= 0) {
                    iterator.remove();
                    continue;
                }

                float remainingSeconds = remainingTicks / 20.0f;
                String timeStr = formatTime(remainingSeconds);

                String itemName = entry.getKey().getName().getString();
                if (itemName.length() > 12) {
                    itemName = itemName.substring(0, 12) + "..";
                }

                ESP.drawItemStack(new ItemStack(entry.getKey()), (x + 3), (y + 21.5f + (index * 11) + 1f), false, true, 0.65f);
                f.drawString(itemName, x + 14, y + 21.5f + (index * 11) + 2.5f + 3, ColorUtils.rgba(180, 180, 180, alpha));
                f.drawString(timeStr, x + 100 - 8 - f.getStringWidth(timeStr), y + 21.5f + (index * 11) + 2.5f + 3, ColorUtils.setAlpha(Essence.getHandler().themeManager.getColor(0), alpha));

                index += 1;
            }
        }

        RenderSystem.popMatrix();

        CooldownsDrag.setWidth(hasCooldowns ? 100 : 80);
        CooldownsDrag.setHeight(hasCooldowns ? (20 + animatedHcooldowns) : 16);
    }

    private String formatTime(float seconds) {
        int mins = (int) (seconds / 60);
        int secs = (int) (seconds % 60);
        return String.format("%02d:%02d", mins, secs);
    }
}
