package beame.util.glu;

public class Util {
// leaked by itskekoff; discord.gg/sk3d jyMLHKxJ

    protected static int ceil(int a, int b) {
        return a % b == 0 ? a / b : a / b + 1;
    }

    protected static float[] normalize(float[] v) {
        float r = (float) Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
        if ((double) r == 0.0) {
            return v;
        }
        r = 1.0f / r;
        v[0] = v[0] * r;
        v[1] = v[1] * r;
        v[2] = v[2] * r;
        return v;
    }

    protected static void cross(float[] v1, float[] v2, float[] result) {
        result[0] = v1[1] * v2[2] - v1[2] * v2[1];
        result[1] = v1[2] * v2[0] - v1[0] * v2[2];
        result[2] = v1[0] * v2[1] - v1[1] * v2[0];
    }
}