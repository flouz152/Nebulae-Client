package beame.util.glu;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class GLU {
// leaked by itskekoff; discord.gg/sk3d rAoyhjsy
    public static boolean gluProject(float objx, float objy, float objz, FloatBuffer modelMatrix, FloatBuffer projMatrix, IntBuffer viewport, FloatBuffer win_pos) {
        return Project.gluProject(objx, objy, objz, modelMatrix, projMatrix, viewport, win_pos);
    }
}

