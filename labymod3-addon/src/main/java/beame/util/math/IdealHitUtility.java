/*
package beame.util.math;

import beame.Essence;
import beame.components.modules.combat.Aura;
import beame.components.modules.combat.ElytraTarget;
import beame.util.ClientHelper;
import beame.util.IMinecraft;
import beame.util.player.PlayerUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Items;
import net.minecraft.item.ShovelItem;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

@UtilityClass
public class IdealHitUtility implements IMinecraft {

    @Setter
    @Getter
    private boolean jumped;

    public float getAICooldown() {



        if (mc.player.getHeldItemMainhand().getItem() == Items.AIR) {
            return 1;
        }

        if (mc.player.isPotionActive(Effects.BLINDNESS)
                || mc.player.isPotionActive(Effects.LEVITATION)
                || mc.player.isPotionActive(Effects.SLOW_FALLING)
                || mc.player.isInLava()
                || mc.player.isInWater()
                || mc.player.isOnLadder()
                || mc.player.isPassenger()
                || mc.world.getBlockState(mc.player.getPosition()).getMaterial() == Material.WEB
                || mc.player.isElytraFlying()
                || mc.player.abilities.isFlying
                || !Essence.getHandler().getModuleList().aura.getOnlycrit().get())
            //|| Server.isRW())
            return 0.944f;

        //if (isBlockBelow())
        //	return 0.93f;

        if (mc.player.getHeldItemMainhand().getItem() instanceof AxeItem || mc.player.getHeldItemMainhand().getItem() instanceof ShovelItem) return 0.99f;

        ElytraTarget elytraTarget = Essence.getHandler().getModuleList().elytraTarget;

        if (elytraTarget.isState())// || criticals)
            return 1;

        return 0.944f;
    }

    public float getAIFallDistance() {

        //if (!jumped) // Если игрок спрыгивает с блока
        //	return 0.01f;

        //if (isBlockBelow())
        //	return 0.05f;

        if (ClientHelper.isConnectedToServer("spooky")) {
            return Essence.getHandler().getModuleList().aura.getAttacks() % Math.round(MathUtil.random(4, 10)) == 0 ? (float)MathUtil.random(0.15, 0.23) : 0f;
        }

        return 0;
    }

    public float getNewFallDistance(LivingEntity target) {
        if (Essence.getHandler().getModuleList().aura.rotationType.get("СпукиТайм")) //|| Server.is("spooky") && Player.collideWith(target))
            return MathUtil.random(0, 0.3f);

        Aura aura = Essence.getHandler().getModuleList().aura;

        if (ClientHelper.isConnectedToServer("spooky")) {
            int attacks = Essence.getHandler().getModuleList().aura.getAttacks();

            if (PlayerUtil.collideWith(target) && !(PlayerUtil.getBlock(0, 2, 0) != Blocks.AIR && PlayerUtil.getBlock(0, -1, 0) != Blocks.AIR && mc.gameSettings.keyBindJump.isKeyDown())) {
                return aura.getFdCount() >= 10 ? 0.3f : 0.15f;
            }

            return aura.getFdCount() >= 10 ? 0.15f : 0;
        }

        //if (rock.getModules().get(Criticals.class).get() && Server.isRW()) {
           // return 1;
     //   }

        return 0;
    }

    public boolean canAIFall() {
        Aura aura = Essence.getHandler().getModuleList().aura;

        if (PlayerUtil.getBlock(0, 2, 0) != Blocks.AIR && PlayerUtil.getBlock(0, -1, 0) != Blocks.AIR && ClientHelper.isConnectedToServer("spooky") && aura.getFdCount() > 8) {
            return false;
        }

        return ((PlayerUtil.getBlock(0, 3, 0) == Blocks.AIR && PlayerUtil.getBlock(0, 2, 0) == Blocks.AIR && PlayerUtil.getBlock(0, 1, 0) == Blocks.AIR)
                || mc.player.fallDistance < (PlayerUtil.getBlock(0, 2, 0) != Blocks.AIR ? 0.08f : 0.6f)
                || mc.player.fallDistance > 1.2f);
    }

    private boolean isBlockBelow() {
        Vector3d pos = mc.player.getPositionVec().add(0, -1, 0);
        AxisAlignedBB hitbox = mc.player.getBoundingBox();

        float off = 0.15f;

        return !isAir(hitbox.minX-off, pos.y, hitbox.minZ-off)
                || !isAir(hitbox.maxX+off, pos.y, hitbox.minZ-off)
                || !isAir(hitbox.minX-off, pos.y, hitbox.maxZ+off)
                || !isAir(hitbox.maxX+off, pos.y, hitbox.maxZ+off);
    }

    private boolean isAir(double x, double y, double z) {
        return mc.world.getBlockState(new BlockPos(x, y, z)).getBlock() == Blocks.AIR;
    }

}
*/
// leaked by itskekoff; discord.gg/sk3d HUILafu9
