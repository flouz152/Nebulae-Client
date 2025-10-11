package mdk.by.ghostbitbox.modules.render.targethud;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import mdk.by.ghostbitbox.TargetEspConfig;
import mdk.by.ghostbitbox.util.ColorUtil;
import mdk.by.ghostbitbox.util.HudRenderUtil;
import mdk.by.ghostbitbox.util.animation.AnimationMath;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class TargetHudRenderer {

    private static final Minecraft MC = Minecraft.getInstance();

    private LivingEntity activeTarget;
    private float animationProgress;
    private float healthAnimated;
    private float delayedHealthAnimated;

    private boolean enabled = true;
    private float anchorX = 0.65f;
    private float anchorY = 0.7f;
    private float width = 118.0f;
    private float height = 44.0f;
    private float barHeight = 6.0f;
    private float itemScale = 0.65f;
    private boolean showEquipment = true;
    private boolean showOffhand = true;
    private boolean showHealthText = true;
    private int backgroundColor = ColorUtil.rgba(14, 14, 18, 160);
    private int outlineColor = ColorUtil.rgba(255, 255, 255, 35);
    private int barBackgroundColor = ColorUtil.rgba(30, 30, 40, 180);
    private int textColor = ColorUtil.rgba(235, 235, 245, 255);
    private int baseColor = ColorUtil.rgba(120, 190, 255, 255);
    private int hurtColor = ColorUtil.rgba(220, 80, 80, 255);
    private boolean hurtTint = true;

    public void applyConfiguration(TargetEspConfig config) {
        if (config == null) {
            return;
        }
        enabled = config.isTargetHudEnabled();
        anchorX = MathHelper.clamp(config.getTargetHudAnchorX(), 0.0f, 1.0f);
        anchorY = MathHelper.clamp(config.getTargetHudAnchorY(), 0.0f, 1.0f);
        width = Math.max(60.0f, config.getTargetHudWidth());
        height = Math.max(30.0f, config.getTargetHudHeight());
        barHeight = MathHelper.clamp(config.getTargetHudBarHeight(), 2.0f, 16.0f);
        itemScale = MathHelper.clamp(config.getTargetHudItemScale(), 0.4f, 1.2f);
        showEquipment = config.isTargetHudShowEquipment();
        showOffhand = config.isTargetHudShowOffhand();
        showHealthText = config.isTargetHudShowHealthText();
        backgroundColor = config.getTargetHudBackgroundColor();
        outlineColor = config.getTargetHudOutlineColor();
        barBackgroundColor = config.getTargetHudBarBackgroundColor();
        textColor = config.getTargetHudTextColor();
        baseColor = config.getBaseColor();
        hurtColor = config.getHurtColor();
        hurtTint = config.isHurtTintEnabled();
    }

    public void render(MatrixStack stack, Entity entity, float visibility, float partialTicks) {
        if (!enabled || MC == null) {
            resetWhenDisabled();
            return;
        }

        LivingEntity current = entity instanceof LivingEntity ? (LivingEntity) entity : null;
        if (current != null && current.isAlive()) {
            activeTarget = current;
        } else if (activeTarget != null && (!activeTarget.isAlive() || activeTarget.removed)) {
            activeTarget = null;
        }

        boolean hasTarget = activeTarget != null;
        float targetValue = hasTarget ? 1.0f : 0.0f;
        animationProgress = AnimationMath.fast(animationProgress, targetValue, hasTarget ? 8.0f : 12.0f);
        float fade = MathHelper.clamp(animationProgress * MathHelper.clamp(visibility, 0.0f, 1.0f), 0.0f, 1.0f);

        if (fade <= 0.01f) {
            return;
        }

        MainWindow window = MC.getMainWindow();
        if (window == null) {
            return;
        }

        float screenWidth = window.getScaledWidth();
        float screenHeight = window.getScaledHeight();
        float centerX = anchorX * screenWidth;
        float centerY = anchorY * screenHeight;
        float clampedWidth = Math.min(width, screenWidth - 4.0f);
        float clampedHeight = Math.min(height, screenHeight - 4.0f);
        float left = MathHelper.clamp(centerX - clampedWidth / 2.0f, 2.0f, screenWidth - clampedWidth - 2.0f);
        float top = MathHelper.clamp(centerY - clampedHeight / 2.0f, 2.0f, screenHeight - clampedHeight - 2.0f);
        float right = left + clampedWidth;
        float bottom = top + clampedHeight;

        if (!hasTarget) {
            return;
        }

        int fadedBackground = fadeColor(backgroundColor, fade);
        int fadedOutline = fadeColor(outlineColor, fade);
        int fadedBarBackground = fadeColor(barBackgroundColor, fade);
        int fadedText = fadeColor(textColor, fade);

        HudRenderUtil.fill(stack, left, top, clampedWidth, clampedHeight, fadedBackground);
        if (((fadedOutline >> 24) & 0xFF) > 0) {
            float outlineWidth = Math.max(1.0f, clampedWidth);
            float outlineHeight = Math.max(1.0f, clampedHeight);
            HudRenderUtil.fill(stack, left, top, outlineWidth, 1.0f, fadedOutline);
            HudRenderUtil.fill(stack, left, bottom - 1.0f, outlineWidth, 1.0f, fadedOutline);
            HudRenderUtil.fill(stack, left, top, 1.0f, outlineHeight, fadedOutline);
            HudRenderUtil.fill(stack, right - 1.0f, top, 1.0f, outlineHeight, fadedOutline);
        }

        float padding = 6.0f;
        float contentLeft = left + padding;
        float contentRight = right - padding;
        float headerHeight = Math.max(16.0f, clampedHeight - (barHeight + padding));
        float barTop = top + headerHeight;
        float barWidth = Math.max(10.0f, contentRight - contentLeft);

        float targetHealth = getHealth(activeTarget);
        float maxHealth = Math.max(activeTarget.getMaxHealth(), 1.0f);
        healthAnimated = AnimationMath.fast(healthAnimated, targetHealth, 16.0f);
        delayedHealthAnimated = AnimationMath.fast(delayedHealthAnimated, targetHealth, 6.0f);

        float barCurrent = MathHelper.clamp(barWidth * (healthAnimated / maxHealth), 0.0f, barWidth);
        float barDelayed = MathHelper.clamp(barWidth * (delayedHealthAnimated / maxHealth), 0.0f, barWidth);

        HudRenderUtil.fill(stack, contentLeft, barTop, barWidth, barHeight, fadedBarBackground);
        int accent = fadeColor(resolveTargetColor(activeTarget), fade);
        int delayed = ColorUtil.setAlpha(accent, Math.min(255, (int) (((accent >> 24) & 0xFF) * 0.65f)));
        HudRenderUtil.fill(stack, contentLeft, barTop, barDelayed, barHeight, delayed);
        HudRenderUtil.fill(stack, contentLeft, barTop, barCurrent, barHeight, accent);

        float nameX = contentLeft;
        float nameY = top + Math.max(4.0f, (headerHeight - MC.fontRenderer.FONT_HEIGHT) / 2.0f);
        String displayName = buildDisplayName(activeTarget);
        MC.fontRenderer.drawStringWithShadow(stack, displayName, nameX, nameY, fadedText);

        if (showHealthText) {
            String hp = String.format("%s / %s", Math.round(targetHealth), Math.round(maxHealth));
            float hpWidth = MC.fontRenderer.getStringWidth(hp);
            MC.fontRenderer.drawStringWithShadow(stack, hp, contentRight - hpWidth, nameY, fadedText);
        }

        drawEquipment(stack, left, top, fade);
    }

    private void resetWhenDisabled() {
        activeTarget = null;
        animationProgress = AnimationMath.fast(animationProgress, 0.0f, 16.0f);
    }

    private void drawEquipment(MatrixStack stack, float panelLeft, float panelTop, float fade) {
        if (activeTarget == null) {
            return;
        }

        float iconAlpha = MathHelper.clamp(fade, 0.0f, 1.0f);
        float iconSize = 16.0f * itemScale;
        float startX = panelLeft + 6.0f;
        float y = panelTop - iconSize - 4.0f;
        float x = startX;

        if (showEquipment && MC.getItemRenderer() != null) {
            x = drawItem(stack, activeTarget.getHeldItemMainhand(), x, y, iconSize, iconAlpha);
            if (showOffhand) {
                x = drawItem(stack, activeTarget.getHeldItemOffhand(), x, y, iconSize, iconAlpha);
            }
            for (ItemStack stackItem : activeTarget.getArmorInventoryList()) {
                x = drawItem(stack, stackItem, x, y, iconSize, iconAlpha);
            }
        }

        if (activeTarget instanceof AbstractClientPlayerEntity) {
            drawPlayerHead(stack, (AbstractClientPlayerEntity) activeTarget, panelLeft + 6.0f, panelTop + 4.0f, iconSize, iconAlpha);
        }
    }

    private float drawItem(MatrixStack stack, ItemStack stackItem, float x, float y, float size, float alpha) {
        if (stackItem == null || stackItem.isEmpty()) {
            return x;
        }

        RenderSystem.pushMatrix();
        RenderSystem.translatef(x, y, 0.0f);
        RenderSystem.scalef(itemScale, itemScale, 1.0f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, alpha);
        MC.getItemRenderer().renderItemAndEffectIntoGUI(stackItem, 0, 0);
        MC.getItemRenderer().renderItemOverlays(MC.fontRenderer, stackItem, 0, 0);
        RenderSystem.disableBlend();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.popMatrix();
        return x + size + 4.0f;
    }

    private void drawPlayerHead(MatrixStack stack, AbstractClientPlayerEntity player, float x, float y, float size, float alpha) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, alpha);
        MC.getTextureManager().bindTexture(player.getLocationSkin());
        int intSize = Math.round(size);
        AbstractGui.blit(stack, Math.round(x), Math.round(y), intSize, intSize, 8.0f, 8.0f, 8, 8, 64, 64);
        AbstractGui.blit(stack, Math.round(x), Math.round(y), intSize, intSize, 40.0f, 8.0f, 8, 8, 64, 64);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
    }

    private String buildDisplayName(LivingEntity entity) {
        String base = entity.getName().getString();
        if (base.length() > 20) {
            base = base.substring(0, 17) + "...";
        }
        return base;
    }

    private float getHealth(LivingEntity entity) {
        if (entity instanceof PlayerEntity) {
            return MathHelper.clamp(((PlayerEntity) entity).getHealth() + ((PlayerEntity) entity).getAbsorptionAmount(), 0.0f, entity.getMaxHealth() + ((PlayerEntity) entity).getAbsorptionAmount());
        }
        return MathHelper.clamp(entity.getHealth(), 0.0f, entity.getMaxHealth());
    }

    private int resolveTargetColor(LivingEntity entity) {
        if (entity == null) {
            return baseColor;
        }
        if (hurtTint && entity.hurtTime > 0) {
            float progress = MathHelper.clamp(entity.hurtTime / 10.0f, 0.0f, 1.0f);
            return ColorUtil.interpolate(hurtColor, baseColor, 1.0f - progress);
        }
        return baseColor;
    }

    private int fadeColor(int color, float fade) {
        int alpha = (color >> 24) & 0xFF;
        alpha = MathHelper.clamp((int) (alpha * fade), 0, 255);
        return (color & 0x00FFFFFF) | (alpha << 24);
    }
}
