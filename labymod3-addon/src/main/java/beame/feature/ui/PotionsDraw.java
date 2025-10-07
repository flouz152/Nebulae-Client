package beame.feature.ui;

import beame.Essence;
import beame.util.animation.AnimationMath;
import beame.util.color.ColorUtils;
import beame.util.drag.Dragging;
import beame.util.fonts.CustomFont;
import beame.util.fonts.Fonts;
import beame.util.other.PotionUtils;
import beame.util.render.ClientHandler;
import events.impl.render.Render2DEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.renderer.texture.PotionSpriteUploader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import com.mojang.blaze3d.systems.RenderSystem;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static beame.util.IMinecraft.mc;
import static beame.util.color.ColorUtils.interpolateColor;

public class PotionsDraw {
// leaked by itskekoff; discord.gg/sk3d Pa4SmUyk
    public Dragging PotionsDrag = Essence.getHandler().createDraggable("potions", 3, 200);

    float animatedHpots = 16;
    float animatedWpots = 80;
    private float animation = 0;
    private final Map<Effect, Long> effectAppearTimes = new HashMap<>();
    private static final long TEXT_DELAY_MS = 50;

    public void render(Render2DEvent event) {
        float x = PotionsDrag.getX();
        float y = PotionsDrag.getY();

        ArrayList<EffectInstance> effects = new ArrayList<EffectInstance>();
        for (EffectInstance p : mc.player.getActivePotionEffects()) {
            if (!p.isShowIcon()) continue;
            effects.add(p);
            effectAppearTimes.putIfAbsent(p.getPotion(), System.currentTimeMillis());
        }
        effectAppearTimes.keySet().removeIf(eff -> effects.stream().noneMatch(inst -> inst.getPotion() == eff));

        boolean hasEffects = !effects.isEmpty();
        boolean isChatOpen = mc.currentScreen instanceof ChatScreen;
        boolean shouldShow = hasEffects || isChatOpen;
        
        animation = AnimationMath.fast(animation, shouldShow ? 1 : 0, 12);
        if (animation == 0) return;

        int potoff = 0;
        animatedWpots = AnimationMath.fast(animatedWpots, 80 + (hasEffects ? 10 : 0), 8);
        animatedHpots = AnimationMath.fast(animatedHpots, (effects.size()* 11) + (hasEffects ? 6 : 0) - 1, 8);

        RenderSystem.pushMatrix();
        AnimationMath.sizeAnimation(x + (animatedWpots / 2), y + (16 / 2), animation);

        ClientHandler.drawSexyRect(x, y, animatedWpots, 16, Essence.getHandler().getModuleList().hud.rounding.get(), true);
        
        int iconColor = ColorUtils.setAlpha(Essence.getHandler().themeManager.getColor(0), (int)(255 * animation));
        Fonts.ESSENCE_ICONS.get(15).drawString("h", x + animatedWpots - 10 - 5, y + 2 + 5, iconColor);
        
        int titleColor = ColorUtils.setAlpha(new Color(255, 255, 255, 225).getRGB(), (int)(225 * animation));
        Fonts.SUISSEINTL.get(13).drawString("Potions", x + 5, y + 2 + 5, titleColor);
        if (hasEffects) {
        ClientHandler.drawSexyRect(x, y + 20, animatedWpots, animatedHpots, Essence.getHandler().getModuleList().hud.rounding.get(), false);

        for (EffectInstance f : effects) {
            Effect ef = f.getPotion();
            boolean isNegative = (ef == Effects.POISON) || (ef == Effects.BLINDNESS) || (ef == Effects.SLOWNESS) || (ef == Effects.HUNGER) || (ef == Effects.WITHER) || (ef == Effects.SLOW_FALLING) || (ef == Effects.GLOWING) || (ef == Effects.WEAKNESS);
            boolean lowTime = (float)f.getDuration() < 20*20;

            String potionName = I18n.format(f.getEffectName(), new Object[0]);
            String time = PotionUtils.getPotionDurationString(f, 1.0f);
                int alpha = (int)(255 * animation);

                boolean showText = false;
                Long appearTime = effectAppearTimes.get(ef);
                if (appearTime != null && System.currentTimeMillis() - appearTime >= TEXT_DELAY_MS) {
                    showText = true;
                }

            if (f.isShowIcon()) {
                String text = potionName;
                CustomFont fon = Fonts.SUISSEINTL.get(13);

                    if(text.length() > 8) {
                        text = text.substring(0, 8) + ".";
                }

                int red = interpolateColor(ColorUtils.rgba(180, 180, 180, alpha), Color.RED.getRGB(), 0.3f);
                    if (showText) {
                        fon.drawString((text.length() > 8 ? text.substring(0, 8) + ".." : text) + (f.getAmplifier()+1 > 1 ? " " + (f.getAmplifier()+1) : ""), x + 8 + 7, y + 21.5f + potoff + 2.5f + 3, isNegative ? red : ColorUtils.rgba(180, 180, 180, alpha));
                fon.drawString(time, x + animatedWpots - 8 - fon.getStringWidth(time), y + 21.5f + potoff + 2.5f + 3, ColorUtils.setAlpha(lowTime ? red : Essence.getHandler().themeManager.getColor(0), alpha));
                    }

                PotionSpriteUploader potionspriteuploader = mc.getPotionSpriteUploader();
                Effect effect = f.getPotion();
                TextureAtlasSprite textureatlassprite = potionspriteuploader.getSprite(effect);
                mc.getTextureManager().bindTexture(textureatlassprite.getAtlasTexture().getTextureLocation());
                    DisplayEffectsScreen.blit(((Render2DEvent) event).getMatrix(), (int)(x + 5), (int)(y + 21 + potoff + 2.5f), 10, 9, 9, textureatlassprite);

                potoff += 11;
                }
            }
        }

        RenderSystem.popMatrix();

        PotionsDrag.setWidth(80);
        PotionsDrag.setHeight(17);
    }
}
