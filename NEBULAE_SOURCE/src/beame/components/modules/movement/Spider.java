package beame.components.modules.movement;

import beame.module.Category;
import beame.module.Module;
import beame.setting.SettingList.BooleanSetting;
import beame.setting.SettingList.SliderSetting;
import events.Event;
import events.impl.player.EventUpdate;
import net.minecraft.block.BlockState;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

public class Spider extends Module {
    private final SliderSetting climbSpeed = new SliderSetting("Скорость подъема", 0.36f, 0.1f, 1.0f, 0.01f);
    private final BooleanSetting requireMovement = new BooleanSetting("Только при движении", true);

    public Spider() {
        super("Spider", Category.Movement, true, "Позволяет подниматься по стенам.");
        addSettings(climbSpeed, requireMovement);
    }

    @Override
    public void event(Event event) {
        if (!(event instanceof EventUpdate) || mc.player == null || mc.world == null) {
            return;
        }

        ClientPlayerEntity player = mc.player;

        if (!player.collidedHorizontally || player.isOnLadder() || player.isElytraFlying() || player.isSpectator()) {
            return;
        }

        if (requireMovement.get() && player.moveForward == 0.0F && player.moveStrafing == 0.0F) {
            return;
        }

        if (!hasClimbableSurface(player)) {
            return;
        }

        Vector3d motion = player.getMotion();
        double upward = Math.max(motion.y, climbSpeed.get());
        player.setMotion(motion.x, upward, motion.z);
        player.fallDistance = 0.0F;
    }

    private boolean hasClimbableSurface(ClientPlayerEntity player) {
        Direction facing = player.getHorizontalFacing();
        BlockPos base = player.getPosition();
        BlockPos frontFeet = base.offset(facing);
        BlockPos frontHead = base.up().offset(facing);
        return isSolid(frontFeet) || isSolid(frontHead);
    }

    private boolean isSolid(BlockPos pos) {
        BlockState state = mc.world.getBlockState(pos);
        return !state.getCollisionShape(mc.world, pos).isEmpty();
    }
}
