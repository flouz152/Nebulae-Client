package beame.components.modules.render;

import beame.Nebulae;
import beame.util.color.ColorUtils;
import beame.util.math.MathUtil;
import beame.util.render.ClientHandler;
import com.mojang.blaze3d.platform.GlStateManager;
import events.Event;
import events.impl.render.Render2DEvent;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import beame.setting.SettingList.BooleanSetting;

import static beame.util.color.ColorUtils.interpolate;

public class Arrows extends Module {
// leaked by itskekoff; discord.gg/sk3d ciLCEfMN
    public final BooleanSetting ignore = new BooleanSetting("Игнорировать голых", false);
    public Arrows() {
        super("Arrows", Category.Visuals, true, "Включает показ стрелок за игроками");
        addSettings(ignore);
    }
    public float animationStep;
    public float animatedRotation;

    @Override
    public void event(Event event) {
        if (event instanceof Render2DEvent ) {
            if (mc.player == null || mc.world == null)
                return;

            float size = 60;

            if (mc.currentScreen instanceof InventoryScreen || mc.currentScreen instanceof ChestScreen) {
                size += 100;
            }

            animationStep = MathUtil.fast(animationStep, size, 12);
            for (AbstractClientPlayerEntity player : mc.world.getPlayers()) {
                if (mc.player == player)
                    continue;
                
                if(!player.botEntity) continue;
                
                if (ignore.get() && isNaked(player)) continue;

                double x = player.lastTickPosX + (player.getPosX() - player.lastTickPosX) * mc.getRenderPartialTicks()
                        - mc.getRenderManager().info.getProjectedView().getX();
                double z = player.lastTickPosZ + (player.getPosZ() - player.lastTickPosZ) * mc.getRenderPartialTicks()
                        - mc.getRenderManager().info.getProjectedView().getZ();

                double cos = MathHelper.cos((float) (mc.getRenderManager().info.getYaw() * (Math.PI * 2 / 360)));
                double sin = MathHelper.sin((float) (mc.getRenderManager().info.getYaw() * (Math.PI * 2 / 360)));
                double rotY = -(z * cos - x * sin);
                double rotX = -(x * cos + z * sin);

//                Hello

                float angle = (float) (Math.atan2(rotY, rotX) * 180 / Math.PI);

                double x2 = animationStep * MathHelper.cos((float) Math.toRadians(angle)) + mc.getMainWindow().getScaledWidth() / 2f;
                double y2 = animationStep * MathHelper.sin((float) Math.toRadians(angle)) + mc.getMainWindow().getScaledHeight() / 2f;

                GlStateManager.pushMatrix();
                GlStateManager.disableBlend();
                GlStateManager.translated(x2, y2, 0);
                GlStateManager.rotatef(angle+90, 0, 0, 1f);
                ClientHandler.drawImage(((Render2DEvent) event).getMatrix(), new ResourceLocation("night/image/arrow.png"), -8.0F, -9.0F, 16, 16, (Nebulae.getHandler().friends.isFriend(player.getScoreboardName()) ? ColorUtils.rgba(80, 250, 80, 255) : Nebulae.getHandler().themeManager.getColor((int)animatedRotation)), 0);
                GlStateManager.enableBlend();
                GlStateManager.popMatrix();
            }
        }
    }

    private boolean isNaked(AbstractClientPlayerEntity player) {
        return player.getItemStackFromSlot(EquipmentSlotType.HEAD).isEmpty()
                && player.getItemStackFromSlot(EquipmentSlotType.CHEST).isEmpty()
                && player.getItemStackFromSlot(EquipmentSlotType.LEGS).isEmpty()
                && player.getItemStackFromSlot(EquipmentSlotType.FEET).isEmpty();
    }
}

