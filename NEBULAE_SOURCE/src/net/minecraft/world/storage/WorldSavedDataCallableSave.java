package net.minecraft.world.storage;

public class WorldSavedDataCallableSave implements Runnable
{
// leaked by itskekoff; discord.gg/sk3d DwU3707K
    private final WorldSavedData data;

    public WorldSavedDataCallableSave(WorldSavedData dataIn)
    {
        this.data = dataIn;
    }

    public void run()
    {
        this.data.markDirty();
    }
}
