package net.minecraft.client.util;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryUtil;

public class NativeUtil
{
// leaked by itskekoff; discord.gg/sk3d 3FiZlOsF
    public static void crash()
    {
        MemoryUtil.memSet(0L, 0, 1L);
    }

    public static double getTime()
    {
        return GLFW.glfwGetTime();
    }
}
