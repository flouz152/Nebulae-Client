package beame.feature.ui;

import beame.Nebulae;
import beame.components.modules.render.NameProtect;
import beame.util.Scissor;
import beame.util.animation.AnimationMath;
import beame.util.color.ColorUtils;
import beame.util.drag.Dragging;
import beame.util.fonts.Fonts;
import beame.util.funtime.HealthUtil;
import beame.util.math.RaytraceUtility;
import beame.util.render.ClientHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MainWindow;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import static beame.util.IMinecraft.mc;

public class TargetHudDraw {
// leaked by itskekoff; discord.gg/sk3d 1NNVoWMN
    public Dragging THDrag = Nebulae.getHandler().createDraggable("targethud", 438, 400);

    public NameProtect nameProtect = new NameProtect();

    float animation;
    float hpAnimated = 0;
    float secondHpAnimated = 0;

    private void drawItemStack(ItemStack stack, float x, float y, boolean overlay, boolean scale, float scaleValue) {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);

        RenderSystem.translatef(x, y, 0);
        if (scale) GL11.glScaled(scaleValue, scaleValue, scaleValue);

        mc.getItemRenderer().renderItemAndEffectIntoGUI(stack, 0, 0);

        if (overlay) mc.getItemRenderer().renderItemOverlays(mc.fontRenderer, stack, 0, 0);

        GL11.glScaled(1, 1, 1);
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
    }

    public void render() {
        MainWindow window = mc.getMainWindow();

        float percentX = THDrag.getX() / window.getScaledWidth();
        float percentY = THDrag.getY() / window.getScaledHeight();

        float x = percentX * window.getScaledWidth();
        float y = percentY * window.getScaledHeight();

        if(Nebulae.getHandler().getModuleList().aura == null)
            return;

        LivingEntity target = mc.currentScreen instanceof ChatScreen && Nebulae.getHandler().getModuleList().aura.getTarget() == null ? mc.player : Nebulae.getHandler().getModuleList().aura.getTarget();
        boolean targetActive = target != null;
        assert mc.player != null;
        Entity over = RaytraceUtility.getMouseOver(mc.player, mc.player.rotationYaw, mc.player.rotationPitch, 3.0f);
        if(!targetActive && over instanceof PlayerEntity) {
            target = (LivingEntity)over;
            targetActive = true;
        }

        if (!targetActive && animation <= 0.2) {
            animation = 0;
            return;
        }

        animation = AnimationMath.fast(animation, targetActive ? 1 : 0, targetActive ? 8 : 16);

        String playerName = mc.getSession().getUsername().length() > 8 ? mc.getSession().getUsername().substring(0, 8) + "..." : mc.getSession().getUsername();
        String nameInput = Nebulae.getHandler().getModuleList().nameProtect.nameInput.get();

        String name = targetActive ? (target instanceof PlayerEntity ?
                (target == mc.player ?
                        (Nebulae.getHandler().getModuleList().nameProtect.isState() ?
                                (nameInput.length() > 10 ? nameInput.substring(0, 9) + "..." : nameInput) : playerName)
                        : target.getName().getString().length() > 8 ? target.getName().getString().substring(0, 8) : target.getName().getString())
                : target.getName().getString())
                : "None";

        float x_size = 80;
        float y_size = 14.5f;
        float y_size2 = 26;

        float hp = targetActive ? target instanceof PlayerEntity ? HealthUtil.getHealth((PlayerEntity)target) : target.getHealth() : 20f;
        float maxHealth = !targetActive ? 20 : target.getMaxHealth();

        String strHp = Math.round(hp) + "";
        float strHpSize = Fonts.SUISSEINTL.get(15).getStringWidth(strHp);

        hpAnimated = AnimationMath.fast(hpAnimated, hp,  16);
        secondHpAnimated = AnimationMath.fast(secondHpAnimated, hp,  6);

        RenderSystem.pushMatrix();
        AnimationMath.sizeAnimation(x + (x_size / 2), y + (y_size / 2), animation);

        ItemStack Main = targetActive ? target.getHeldItemMainhand() : null;
        ItemStack Second = targetActive ? target.getHeldItemOffhand() : null;
        Iterable<ItemStack> Armor = targetActive ? target.getArmorInventoryList() : null;
        float add = 0;
        if(targetActive) {
            if (!Main.isEmpty()) {
                drawItemStack(Main, (x + 4 + add), (y - 13), true, true, 0.6f);
                add += 10;
            }
            if (!Second.isEmpty()) {
                drawItemStack(Second, (x + 4 + add), (y - 13), true, true, 0.6f);
                add += 10;
            }
            for (ItemStack ar : Armor) {
                if (!ar.isEmpty()) {
                    drawItemStack(ar, (x + 4 + add), (y - 13), true, true, 0.6f);
                    add += 10;
                }
            }
        }

        if (animation > 0.2) {
            ClientHandler.drawSexyRect(x, y, x_size, y_size, Nebulae.getHandler().getModuleList().hud.rounding.get(), true);
            ClientHandler.drawSexyRect(x, y + 6 + y_size2 - y_size, x_size, 13, Nebulae.getHandler().getModuleList().hud.rounding.get(), false);

            float bar_size = Math.min((x_size - 6)*(hpAnimated/maxHealth), (x_size - 6));
            float bar_size2 = Math.min((x_size - 6)*(secondHpAnimated/maxHealth), (x_size - 6));

            int accent1 = Nebulae.getHandler().themeManager.getColor(0);
            int accent1_ddark = ColorUtils.interpolateColor(Nebulae.getHandler().themeManager.getColor(0), 3, 0.75f);
            int accent1_dark = ColorUtils.interpolateColor(Nebulae.getHandler().themeManager.getColor(0), 3, 0.45f);

            ClientHandler.drawRound(x + 3, y + (y_size2 - 4f), x_size - 6, 4, 1, accent1_dark);
            ClientHandler.drawRound(x + 3, y + (y_size2 - 4f), bar_size2, 4, 1, accent1_ddark);
            ClientHandler.drawRound(x + 3, y + (y_size2 - 4f), bar_size, 4, 1, accent1);

            MatrixStack matrixStack = new MatrixStack();

            if (target instanceof AbstractClientPlayerEntity) {
                ClientHandler.drawPlayerIcon(x + 5, y + 3f, 8F, 8F, 8F, 8F, 8, 8, 64F, 64F, (AbstractClientPlayerEntity) target);
            } else {
                Fonts.ESSENCE_ICONS.get(16).drawString(matrixStack, "1", x + 4f, y + 6f, Nebulae.getHandler().getThemeManager().getColor(0));
            }

            Scissor.push();
            Scissor.setFromComponentCoordinates(x, y, x_size - 20, y_size);
            Fonts.SUISSEINTL.get(14).drawString(name, x + 15.5f, y + 5.5f, -1);
            Scissor.unset();
            Scissor.pop();
            Fonts.SUISSEINTL.get(14).drawString(strHp, x + x_size - strHpSize - 5 - 3, y + 6, Nebulae.getHandler().themeManager.getColor(180));
        }

        RenderSystem.popMatrix();

        THDrag.setX(x);
        THDrag.setY(y);
        THDrag.setWidth(x_size);
        THDrag.setHeight(y_size);
    }
}
