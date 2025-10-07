package beame.components.modules.combat;

import beame.util.other.ViaUtil;
import events.Event;
import events.impl.player.EventUpdate;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector2f;
import beame.setting.SettingList.SliderSetting;

import java.util.Comparator;

public class FastBow extends Module {
// leaked by itskekoff; discord.gg/sk3d AQn5g1Ic
    private final SliderSetting distance = new SliderSetting( "Дистанция", 10, 6, 20, 1);
    private final SliderSetting delay = new SliderSetting( "Задержка", 6, 3, 15, 1);
    PlayerEntity target;
    public FastBow() {
        super("FastBow", Category.Combat, true, "Выстрел с лука с установкой задержки");
        addSettings (distance, delay);
    }

    @Override
    public void event(Event e) {
        if(e instanceof EventUpdate event) {
            if (mc.player.getActiveItemStack().getItem() == Items.BOW && mc.player.getItemInUseMaxCount() > delay.get()) {
                target = findTarget();
                if (target == null) return;
                Vector2f rotation = calculateRotation(target);
                ViaUtil.sendPositionPacket(rotation.x, rotation.y, false);
                mc.player.connection.sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.RELEASE_USE_ITEM, BlockPos.ZERO, Direction.DOWN));
                mc.player.stopActiveHand();
                ViaUtil.sendPositionPacket(mc.player.rotationYaw, mc.player.rotationPitch, true);
            }
        }
    }

    public PlayerEntity findTarget() {
        return mc.world.getPlayers().stream().filter(p -> p != mc.player && mc.player.canEntityBeSeen(p) && mc.player.getDistance(p) < distance.get()).min(Comparator.comparingDouble(p -> mc.player.getDistance(p))).orElse(null);
    }

    public Vector2f calculateRotation(PlayerEntity target) {
        float currentDuration = (mc.player.getItemInUseCount() - mc.player.getItemInUseMaxCount()) / 20.0F;
        currentDuration = (currentDuration * currentDuration + currentDuration * 2.0f) / 3.0f;
        if (currentDuration >= 1.0f) currentDuration = 1.0f;
        float pitch = (float) (-Math.toDegrees(calculateArc(target, currentDuration * 3.0f)));
        double iX = target.getPosX() - target.prevPosX;
        double iZ = target.getPosZ() - target.prevPosZ;
        double distance = mc.player.getDistance(target);
        distance -= distance % 2.0;
        iX = distance / 2.0 * iX * (mc.player.isSprinting() ? 1.3 : 1.1);
        iZ = distance / 2.0 * iZ * (mc.player.isSprinting() ? 1.3 : 1.1);
        float rotationYaw = (float) Math.toDegrees(Math.atan2(target.getPosZ() + iZ - mc.player.getPosZ(), target.getPosX() + iX - mc.player.getPosX())) - 90.0f;
        return new Vector2f(rotationYaw, pitch);
    }

    private float calculateArc(PlayerEntity target, double duration) {
        double yArc = target.getPosY() + (double) (target.getEyeHeight(target.getPose())) - (mc.player.getPosY() + (double) mc.player.getEyeHeight(mc.player.getPose()));
        double dX = target.getPosX() - mc.player.getPosX();
        double dZ = target.getPosZ() - mc.player.getPosZ();
        double dirRoot = Math.sqrt(dX * dX + dZ * dZ);
        return calculateArc(duration, dirRoot, yArc);
    }

    private float calculateArc(double d, double dr, double y) {
        y = 2.0 * y * (d * d);
        y = 0.05 * ((0.05 * (dr * dr)) + y);
        y = Math.sqrt(d * d * d * d - y);
        d = d * d - y;
        y = Math.atan2(d * d + y, 0.05 * dr);
        d = Math.atan2(d, 0.05 * dr);
        return (float) Math.min(y, d);
    }
}