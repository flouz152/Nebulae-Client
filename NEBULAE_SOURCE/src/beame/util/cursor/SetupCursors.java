package beame.util.cursor;

import org.lwjgl.glfw.GLFW;

public class SetupCursors {
// leaked by itskekoff; discord.gg/sk3d b6xAatgY

    public static long ARROW = GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR);
    public static long HAND = GLFW.glfwCreateStandardCursor(GLFW.GLFW_HAND_CURSOR);
    public static long RESIZEH = GLFW.glfwCreateStandardCursor(GLFW.GLFW_HRESIZE_CURSOR);
    public static long IBEAM = GLFW.glfwCreateStandardCursor(GLFW.GLFW_IBEAM_CURSOR);

}

