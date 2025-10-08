package beame.components.modules.player.AutoExpress;

import beame.components.command.AbstractCommand;
import events.impl.player.EventMotion;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

import static beame.util.IMinecraft.mc;

public class ChestRotation {
// leaked by itskekoff; discord.gg/sk3d n0ynUw40
    public ChestRotation() { }

    public float[] rotations(TileEntity entity) {
        Vector3d playerPos = mc.player.getPositionVec();
        Vector3d entityPos = Vector3d.copyCentered(entity.getPos());

        double x = entityPos.x - playerPos.x;
        double y = entityPos.y - playerPos.y - 1.5;
        double z = entityPos.z - playerPos.z;

        double distanceXZ = Math.sqrt(x * x + z * z);
        float yaw = (float) (Math.atan2(z, x) * (180 / Math.PI)) - 90.0F;
        float pitch = (float) (-(Math.atan2(y, distanceXZ) * (180 / Math.PI)));
        return new float[]{yaw, pitch};
    }

    public int ticks = 0;

    public void rotate(EventMotion motion) {
        for (TileEntity te : mc.world.loadedTileEntityList) {
            if (mc.currentScreen == null) {
                if (te instanceof ChestTileEntity) {
                    BlockPos pos = te.getPos();
                    double distanceSq = mc.player.getPositionVec().distanceTo(Vector3d.copyCentered(pos));
                    if (distanceSq <= 20.25f/3) {
                        ticks++;
                        if (ticks > 20) {
                            ticks = 0;
                        }
                        if (ticks > 0) {
                            float[] rotate = rotations(te);
                            AbstractCommand.addMessage("[debug] chest (distance " + String.format("%.2f", distanceSq) + ") (pos " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ") (rotate " + rotate[0] + ", " + rotate[1] + ")");
                            motion.setYaw(rotate[0]);
                            motion.setPitch(rotate[1]);
                        }

                        break;
                    }
                }
            }
        }
    }
}
