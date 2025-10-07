package beame.util.glu;

import net.minecraft.util.math.MathHelper;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Project extends Util {
// leaked by itskekoff; discord.gg/sk3d FKBG6tGq
    private static final float[] IDENTITY_MATRIX = new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f};
    private static final FloatBuffer matrix = BufferUtils.createFloatBuffer(16);
    private static final FloatBuffer finalMatrix = BufferUtils.createFloatBuffer(16);
    private static final FloatBuffer tempMatrix = BufferUtils.createFloatBuffer(16);
    private static final float[] in = new float[4];
    private static final float[] out = new float[4];
    private static void __gluMakeIdentityf(FloatBuffer m) {
        int oldPos = m.position();
        m.put(IDENTITY_MATRIX);
        m.position(oldPos);
    }

    private static void __gluMultMatrixVecf(FloatBuffer m, float[] in, float[] out) {
        for (int i = 0; i < 4; ++i) {
            out[i] = in[0] * m.get(m.position() + i) + in[1] * m.get(m.position() + 4 + i) + in[2] * m.get(m.position() + 8 + i) + in[3] * m.get(m.position() + 12 + i);
        }
    }

    private static boolean __gluInvertMatrixf() {
        int i;
        FloatBuffer temp = tempMatrix;
        for (i = 0; i < 16; ++i) {
            temp.put(i, finalMatrix.get(i + finalMatrix.position()));
        }
        Project.__gluMakeIdentityf(finalMatrix);
        for (i = 0; i < 4; ++i) {
            float t;
            int k;
            int j;
            int swap = i;
            for (j = i + 1; j < 4; ++j) {
                if (!(Math.abs(temp.get(j * 4 + i)) > Math.abs(temp.get(i * 4 + i)))) continue;
                swap = j;
            }
            if (swap != i) {
                for (k = 0; k < 4; ++k) {
                    t = temp.get(i * 4 + k);
                    temp.put(i * 4 + k, temp.get(swap * 4 + k));
                    temp.put(swap * 4 + k, t);
                    t = finalMatrix.get(i * 4 + k);
                    finalMatrix.put(i * 4 + k, finalMatrix.get(swap * 4 + k));
                    finalMatrix.put(swap * 4 + k, t);
                }
            }
            if (temp.get(i * 4 + i) == 0.0f) {
                return false;
            }
            t = temp.get(i * 4 + i);
            for (k = 0; k < 4; ++k) {
                temp.put(i * 4 + k, temp.get(i * 4 + k) / t);
                finalMatrix.put(i * 4 + k, finalMatrix.get(i * 4 + k) / t);
            }
            for (j = 0; j < 4; ++j) {
                if (j == i) continue;
                t = temp.get(j * 4 + i);
                for (k = 0; k < 4; ++k) {
                    temp.put(j * 4 + k, temp.get(j * 4 + k) - temp.get(i * 4 + k) * t);
                    finalMatrix.put(j * 4 + k, finalMatrix.get(j * 4 + k) - finalMatrix.get(i * 4 + k) * t);
                }
            }
        }
        return true;
    }

    private static void __gluMultMatricesf(FloatBuffer a, FloatBuffer b) {
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                finalMatrix.put(finalMatrix.position() + i * 4 + j, a.get(a.position() + i * 4) * b.get(b.position() + j) + a.get(a.position() + i * 4 + 1) * b.get(b.position() + 4 + j) + a.get(a.position() + i * 4 + 2) * b.get(b.position() + 8 + j) + a.get(a.position() + i * 4 + 3) * b.get(b.position() + 12 + j));
            }
        }
    }

    public static void gluPerspective(float fovy, float aspect, float zNear, float zFar) {
        float radians = (float)((double)(fovy / 2.0f) * Math.PI / 180.0);
        float deltaZ = zFar - zNear;
        float sine = MathHelper.sin(radians);
        if (deltaZ == 0.0f || sine == 0.0f || aspect == 0.0f) {
            return;
        }
        float cotangent = MathHelper.cos(radians) / sine;
        Project.__gluMakeIdentityf(matrix);
        matrix.put(0, cotangent / aspect);
        matrix.put(5, cotangent);
        matrix.put(10, -(zFar + zNear) / deltaZ);
        matrix.put(11, -1.0f);
        matrix.put(14, -2.0f * zNear * zFar / deltaZ);
        matrix.put(15, 0.0f);
        GL11.glMultMatrixf(matrix);
    }

    public static boolean gluProject(float objx, float objy, float objz, FloatBuffer modelMatrix, FloatBuffer projMatrix, IntBuffer viewport, FloatBuffer win_pos) {
        float[] in = new float[]{objx, objy, objz, 1.0f};
        float[] out = new float[4];
        Project.__gluMultMatrixVecf(modelMatrix, in, out);
        Project.__gluMultMatrixVecf(projMatrix, out, in);
        if (in[3] == 0.0f) {
            return false;
        }
        float invW = 1.0f / in[3];
        in[0] = (in[0] * invW + 1.0f) * 0.5f;
        in[1] = (in[1] * invW + 1.0f) * 0.5f;
        in[2] = in[2] * invW;
        int viewportOffset = viewport.position();
        int viewportX = viewport.get(viewportOffset);
        int viewportY = viewport.get(viewportOffset + 1);
        int viewportW = viewport.get(viewportOffset + 2);
        int viewportH = viewport.get(viewportOffset + 3);
        win_pos.put(0, in[0] * (float)viewportW + (float)viewportX);
        win_pos.put(1, in[1] * (float)viewportH + (float)viewportY);
        win_pos.put(2, in[2]);
        return true;
    }

    public static boolean gluUnProject(float winx, float winy, float winz, FloatBuffer modelMatrix, FloatBuffer projMatrix, IntBuffer viewport, FloatBuffer obj_pos) {
        float[] in = Project.in;
        float[] out = Project.out;
        Project.__gluMultMatricesf(modelMatrix, projMatrix);
        if (!Project.__gluInvertMatrixf()) {
            return false;
        }
        in[0] = winx;
        in[1] = winy;
        in[2] = winz;
        in[3] = 1.0f;
        in[0] = (in[0] - (float)viewport.get(viewport.position())) / (float)viewport.get(viewport.position() + 2);
        in[1] = (in[1] - (float)viewport.get(viewport.position() + 1)) / (float)viewport.get(viewport.position() + 3);
        in[0] = in[0] * 2.0f - 1.0f;
        in[1] = in[1] * 2.0f - 1.0f;
        in[2] = in[2] * 2.0f - 1.0f;
        Project.__gluMultMatrixVecf(finalMatrix, in, out);
        if ((double)out[3] == 0.0) {
            return false;
        }
        out[3] = 1.0f / out[3];
        obj_pos.put(obj_pos.position(), out[0] * out[3]);
        obj_pos.put(obj_pos.position() + 1, out[1] * out[3]);
        obj_pos.put(obj_pos.position() + 2, out[2] * out[3]);
        return true;
    }

    public static void gluPickMatrix(float x, float y, float deltaX, float deltaY, IntBuffer viewport) {
        if (deltaX <= 0.0f || deltaY <= 0.0f) {
            return;
        }
        GL11.glTranslatef(((float)viewport.get(viewport.position() + 2) - 2.0f * (x - (float)viewport.get(viewport.position()))) / deltaX, ((float)viewport.get(viewport.position() + 3) - 2.0f * (y - (float)viewport.get(viewport.position() + 1))) / deltaY, 0.0f);
        GL11.glScalef((float)viewport.get(viewport.position() + 2) / deltaX, (float)viewport.get(viewport.position() + 3) / deltaY, 1.0f);
    }
}