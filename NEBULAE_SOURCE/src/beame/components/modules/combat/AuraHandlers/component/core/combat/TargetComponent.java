package beame.components.modules.combat.AuraHandlers.component.core.combat;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;

import static beame.components.modules.combat.AntiBot.isBot;
import static beame.util.IMinecraft.mc;

public class TargetComponent {
// leaked by itskekoff; discord.gg/sk3d 5dfAzcfx
    private static LivingEntity lastTarget;

    public static LivingEntity lastTarget() {
        return lastTarget;
    }

    public static void setLastTarget(LivingEntity target) {
        lastTarget = target;
    }

    public static boolean isValidTarget(LivingEntity entity, float range, boolean walls) {
        if (entity == null || entity instanceof ClientPlayerEntity || !entity.isAlive() || entity.isInvulnerable() || entity instanceof ArmorStandEntity)
            return false;
        if (entity.ticksExisted < 3 || mc.player.getDistanceEyePos(entity) > range) return false;
        if (!walls && !mc.player.canEntityBeSeen(entity)) return false;

        if (entity instanceof PlayerEntity player) {
            if (isBot(player)) return false;
            if (player.getName().getString().equalsIgnoreCase(mc.player.getName().getString())) return false;
        }

        return (!(entity instanceof MonsterEntity)) &&
                (!(entity instanceof AnimalEntity)) &&
                (!(entity instanceof VillagerEntity));
    }

    public static boolean isInFov(LivingEntity target, float fov) {
        if (target == null) return false;
        Vector3d playerDirection = mc.player.getLook(1.0F).normalize();
        Vector3d targetDirection = target.getPositionVec()
                .subtract(mc.player.getEyePosition(1.0F))
                .normalize();
        double dotProduct = playerDirection.dotProduct(targetDirection);
        double angle = Math.toDegrees(Math.acos(Math.max(-1.0, Math.min(dotProduct, 1.0))));
        return angle <= fov;
    }
} 