package beame.util.math;

import beame.util.IMinecraft;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;

public class SensUtils implements IMinecraft {
// leaked by itskekoff; discord.gg/sk3d hiW63vav
    public static class VecRotation {
        private float yaw;
        private float pitch;

        public VecRotation(float yaw, float pitch) {
            this.yaw = yaw;
            this.pitch = pitch;
        }

        public float getYaw() {
            return yaw;
        }
        public void setYaw(float yaw) {
            this.yaw = yaw;
        }
        public float getPitch() {
            return pitch;
        }
        public void setPitch(float pitch) {
            this.pitch = pitch;
        }
    }
}
