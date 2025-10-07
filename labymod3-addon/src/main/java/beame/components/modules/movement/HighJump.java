package beame.components.modules.movement;

import events.Event;
import events.impl.player.EventUpdate;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.block.CarpetBlock;
import net.minecraft.block.SnowBlock;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import beame.setting.SettingList.SliderSetting;

public class HighJump extends Module {
// leaked by itskekoff; discord.gg/sk3d 62VOTS44

    public final SliderSetting velocityY = new SliderSetting("Высота прыжка", 0.5f, 0.2f, .6f, 0.1f);

    public HighJump() {
        super("HighJump", Category.Movement, true, "Устанавливает супер прыжок для игрока");
        addSettings(velocityY);
    }

    @Override
    public void event(Event event) {
        if (event instanceof EventUpdate) {
            if (mc.player.isOnGround()) {
                BlockPos playerPos = mc.player.getPosition();
                if (mc.world.getBlockState(playerPos).getBlock() instanceof CarpetBlock || mc.world.getBlockState(playerPos).getBlock() instanceof SnowBlock) {
                    mc.player.jumpMovementFactor = Math.min(mc.player.jumpMovementFactor + 0.0025f, 0.035f);
                    mc.player.connection.sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.START_DESTROY_BLOCK, playerPos.up(), Direction.UP));
                    mc.player.setVelocity(mc.player.getMotion().x, velocityY.get(), mc.player.getMotion().z);
                }
            }
        }
    }
}
