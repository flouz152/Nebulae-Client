package net.minecraft.client.audio;

public interface ITickableSound extends ISound
{
// leaked by itskekoff; discord.gg/sk3d CYsR9x00
    boolean isDonePlaying();

    void tick();
}
