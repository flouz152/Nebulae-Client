package com.mojang.blaze3d.platform;

public class PlatformDescriptors
{
// leaked by itskekoff; discord.gg/sk3d cCFECzam
    public static String getGlVendor()
    {
        return GlStateManager.getString(7936);
    }

    public static String getCpuInfo()
    {
        return GLX._getCpuInfo();
    }

    public static String getGlRenderer()
    {
        return GlStateManager.getString(7937);
    }

    public static String getGlVersion()
    {
        return GlStateManager.getString(7938);
    }
}
