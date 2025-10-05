package beame.components.modules.movement;

import beame.util.math.MovementUtil;
import beame.util.math.TimerUtil;
import events.Event;
import events.impl.player.EventUpdate;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.potion.Effects;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import beame.setting.SettingList.RadioSetting;

public class Speed extends Module {
// leaked by itskekoff; discord.gg/sk3d 1NLSnAuw
    public Speed() {
        super("Speed", Category.Movement, true, "Позволяет быстро бегать");
        addSettings(mode);
    }

    public RadioSetting mode = new RadioSetting("Тип", "Энтити", "Энтити");

    public boolean isMoving() {
        return mc.player.movementInput.moveForward != 0f || mc.player.movementInput.moveStrafe != 0f;
    }
    TimerUtil stopWatch = new TimerUtil();

    @Override
    public void event(Event event) {
        if(event instanceof EventUpdate){
            if(mode.is("Энтити")) {
                for (PlayerEntity entity : mc.world.getPlayers()) {
                    if (entity != mc.player && mc.player.getDistanceSq(entity) < 2f && isMoving()) {
                        AxisAlignedBB aabb = mc.player.getBoundingBox().grow(0.2);
                        int armorstans = mc.world.getEntitiesWithinAABB(ArmorStandEntity.class, aabb).size();
                        boolean canBoost = armorstans > 1 || mc.world.getEntitiesWithinAABB(LivingEntity.class, aabb).size() > 1;
                        if (canBoost && !mc.player.isOnGround()) {
                            mc.player.jumpMovementFactor = armorstans > 1 ? 1.0f / (float) armorstans : 0.16f;
                        }
                    }
                }
            } else if (mode.is("FunTime On Snow")) {
                Block cur = mc.world.getBlockState(new BlockPos(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ())).getBlock();
                if (cur != Blocks.SNOW && cur != Blocks.GRASS)
                    return;

                mc.player.jumpMovementFactor = 0.035f;
                if (!mc.player.isOnGround()) return;
                if (!MovementUtil.isMoving()) return;

                if (mc.player.collidedHorizontally && mc.gameSettings.keyBindJump.isPressed()) {
                    if (mc.gameSettings.keyBindJump.isPressed()) return;

                    mc.player.jump();
                    return;
                }

                mc.player.connection.sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.START_DESTROY_BLOCK, new BlockPos(mc.player.getPosX(), mc.player.getPosY()-1, mc.player.getPosZ()), Direction.UP));
                mc.player.connection.sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.ABORT_DESTROY_BLOCK, new BlockPos(mc.player.getPosX(), mc.player.getPosY()-1, mc.player.getPosZ()), Direction.UP));
                mc.player.jump();

                mc.player.motion.y /= 6f;
            } else if(mode.is("FunTime On Ground")) {
                if (!isBlockUnderWithMotion() && mc.player.isOnGround() && !mc.player.movementInput.jump && !mc.player.isPotionActive(Effects.SLOWNESS)) {
                    float speed = 1.16f;
                    mc.player.motion.x *= speed;
                    mc.player.motion.z *= speed;
                    if (stopWatch.hasReached(120)) {
                        BlockPos.getAllInBox(mc.player.getBoundingBox().offset(0, -1e-1, 0)).filter(pos -> !mc.world.getBlockState(pos).isAir())
                                .forEach(pos -> mc.player.connection.sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.STOP_DESTROY_BLOCK, pos, Direction.UP)));
                        mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.PRESS_SHIFT_KEY));
                        new Thread(() -> {
                            try {
                                Thread.sleep(5);
                            } catch (InterruptedException egg) {
                                System.out.println(egg.getMessage());
                            }

                            mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.RELEASE_SHIFT_KEY));
                        }).start();
                        stopWatch.reset();
                    }
                }
            }
        }
    }

    public boolean isBlockUnderWithMotion() {
        AxisAlignedBB aab = mc.player.getBoundingBox().offset(mc.player.getMotion().x, -1e-1, mc.player.getMotion().z);
        return mc.world.getCollisionShapes(mc.player, aab).toList().isEmpty();
    }

}
