/*
package beame.components.modules.misc;

import beame.feature.gps.GPS;
import beame.util.math.TimerUtil;
import beame.util.player.InventoryUtility;
import beame.util.player.PlayerUtil;
import events.Event;
import events.impl.player.EventMotion;
import beame.module.Module;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.gen.Heightmap;



public class AutoPilot extends Module {

    private Vector3d target = Vector3d.ZERO;
    private float yaw, pitch;
    private final TimerUtil timer = new TimerUtil();

    private final CheckBox swapChestplate = new CheckBox(this, "Брать нагрудник").desc("Брать нагрудник по прилёту на координаты");

    @Override
    public void onEvent(Event event) {


        if (event instanceof EventMotion e) {
            target = GPS.
            if (target != null && target != Vector3d.ZERO && mc.player.isElytraFlying()) {
                PlayerUtil.look(event, yaw, pitch, true);
                e.setYaw(yaw);
                e.setPitch(pitch);


                Vector3d vec = target.subtract(mc.player.getEyePosition(mc.getRenderPartialTicks())).normalize();
                float rawYaw = (float) Math.toDegrees(Math.atan2(-vec.x, vec.z));
                int highestY = (int) mc.player.getPosY();
                int highestX = (int) target.x;
                int highestZ = (int) target.z;
                int iterations = 60;

                for (int x = -iterations; x < iterations; x++) {
                    for (int z = -iterations; z < iterations; z++) {
                        int height = mc.world.getHeight(Heightmap.Type.WORLD_SURFACE, (int) (mc.player.getPosX() + x), (int) (mc.player.getPosZ() + z)) + 5;

                        if (height > highestY && height > mc.player.getPosY()) {
                            highestY = height;
                            highestX = (int) (mc.player.getPosX() + x);
                            highestZ = (int) (mc.player.getPosZ() + z);
                        }
                    }
                }

                Vector3d vecHeight = new Vector3d(highestX, highestY + 23, highestZ).subtract(mc.player.getEyePosition(mc.getRenderPartialTicks())).normalize();
                float rawPitch = (float) MathHelper.clamp(Math.toDegrees(Math.asin(-vecHeight.y)), -89, 89);

                yaw = rawYaw;
                pitch = rawPitch + 13f;

                mc.gameSettings.keyBindSprint.setPressed(true);
                mc.gameSettings.keyBindForward.setPressed(true);

                if (Move.getSpeed() < 1.46 && timer.getTimePassed(long) (1000 / mc.timer.timerSpeed)) && InventoryUtility.getFirework() != -1) {
                    mc.player.connection.sendPacket(new CHeldItemChangePacket(InventoryUtility.getFirework()));
                    mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                    mc.player.connection.sendPacket(new CHeldItemChangePacket(mc.player.inventory.currentItem));
                    timer.reset();
                }

                for (int i = mc.world.getHeight(Heightmap.Type.WORLD_SURFACE, (int) (mc.player.getPosX()), (int) (mc.player.getPosZ())) - 10; i < mc.player.getPosY(); i++) {
                    if (!mc.world.getBlockState(new BlockPos(mc.player.getPosX(), i, mc.player.getPosZ())).getFluidState().isEmpty() && mc.player.getPosY() - i < 5) {
                        rawPitch -= 11;
                        break;
                    }
                }

                if (mc.player.getDistance(target) < 30) {
                    Chat.msg("Отличная поездка! Спасибо за использование сервиса \"Димамик\"");
                    if (this.swapChestplate.get()) {
                        int item = Inventory.getChestplate();
                        Player.moveItemOld(item < 46 ? item : 6, 6, true);
                    }
                    toggle();
                }
            }
        }

        if (event instanceof EventTrace e) {
            e.setYaw(this.yaw);
            e.setPitch(this.pitch);
            e.cancel();
        }
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onEnable() {

    }
}
*/
// leaked by itskekoff; discord.gg/sk3d qHum3QCP
