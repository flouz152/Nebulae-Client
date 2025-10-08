package beame.components.modules.combat;

import beame.util.math.MovementUtil;
import events.Event;
import events.impl.player.EventUpdate;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.block.Blocks;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import beame.setting.SettingList.RadioSetting;

public class Criticals extends Module {
// leaked by itskekoff; discord.gg/sk3d Uj3GuW1I
    public RadioSetting mode = new RadioSetting("Режим", "FunTime On Snow", "FunTime On Snow");

    public Criticals() {
        super("Criticals", Category.Combat, true, "Пакетные удары критами с места");
        addSettings(mode);
    }

    @Override
    public void event(Event event) {
        if(event instanceof EventUpdate) {
            boolean boost = mc.world.getBlockState(new BlockPos(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ())).getBlock() == Blocks.SNOW;

            if (boost) {
                if (!mc.player.isOnGround()) return;
                if (!MovementUtil.isMoving()) return;
                mc.player.jumpMovementFactor = 0.035f;

                if (mc.player.collidedHorizontally && mc.gameSettings.keyBindJump.isPressed()) {
                    if (mc.gameSettings.keyBindJump.isPressed()) return;

                    mc.player.jump();
                    return;
                }

                mc.player.connection.sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.START_DESTROY_BLOCK, new BlockPos(mc.player.getPosX(), mc.player.getPosY()-1, mc.player.getPosZ()), Direction.UP));
                mc.player.connection.sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.ABORT_DESTROY_BLOCK, new BlockPos(mc.player.getPosX(), mc.player.getPosY()-1, mc.player.getPosZ()), Direction.UP));
                mc.player.jump();

                /*if(mc.player.fallDistance > 0) {
                    mc.player.connection.sendPacket(new CPlayerPacket.RotationPacket(mc.player.rotationYaw, mc.player.rotationPitch, false));
                    CMD.addMessage("[<3] Send packet (3)");
                }*/
                mc.player.motion.y /= 2f;
            }

            /*if(boost && MovementUtil.isMoving()) {
                float horizontal = 0.05f;
                mc.player.motion.x += horizontal;
                mc.player.motion.z += horizontal;
            }*/
        }
    }

}
