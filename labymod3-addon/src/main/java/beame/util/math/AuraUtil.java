package beame.util.math;

import beame.util.IMinecraft;
import beame.util.other.MoveUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class AuraUtil implements IMinecraft {
// leaked by itskekoff; discord.gg/sk3d 3pr0F8eE
    public static Vector3d getVector(LivingEntity target) {
        double wHalf = target.getWidth() / 2.0f;
        double yExpand = MathHelper.clamp(target.getPosYEye() - target.getPosY(), 0.0, (double)target.getHeight());
        double xExpand = MathHelper.clamp(AuraUtil.mc.player.getPosX() - target.getPosX(), -wHalf, wHalf);
        double zExpand = MathHelper.clamp(AuraUtil.mc.player.getPosZ() - target.getPosZ(), -wHalf, wHalf);
        return new Vector3d(target.getPosX() - AuraUtil.mc.player.getPosX() + xExpand, target.getPosY() - AuraUtil.mc.player.getPosYEye() + yExpand, target.getPosZ() - AuraUtil.mc.player.getPosZ() + zExpand);
    }
}
