package net.minecraft.client.renderer;

import java.util.OptionalInt;

public class ScreenSize
{
// leaked by itskekoff; discord.gg/sk3d 9tlRVI5l
    public final int width;
    public final int height;
    public final OptionalInt fullscreenWidth;
    public final OptionalInt fullscreenHeight;
    public final boolean fullscreen;

    public ScreenSize(int widthIn, int heightIn, OptionalInt fullscreenWidthIn, OptionalInt fullscreenHeightIn, boolean fullscreenIn)
    {
        this.width = widthIn;
        this.height = heightIn;
        this.fullscreenWidth = fullscreenWidthIn;
        this.fullscreenHeight = fullscreenHeightIn;
        this.fullscreen = fullscreenIn;
    }
}
