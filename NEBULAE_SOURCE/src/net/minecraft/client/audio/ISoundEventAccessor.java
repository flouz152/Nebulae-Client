package net.minecraft.client.audio;

public interface ISoundEventAccessor<T>
{
// leaked by itskekoff; discord.gg/sk3d mBb6OUZn
    int getWeight();

    T cloneEntry();

    void enqueuePreload(SoundEngine engine);
}
