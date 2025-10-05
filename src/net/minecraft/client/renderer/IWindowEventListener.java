package net.minecraft.client.renderer;

public interface IWindowEventListener
{
// leaked by itskekoff; discord.gg/sk3d 4lmztfjn
    void setGameFocused(boolean focused);

    void updateWindowSize();

    void ignoreFirstMove();
}
