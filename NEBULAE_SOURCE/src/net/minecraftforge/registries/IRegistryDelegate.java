package net.minecraftforge.registries;

import net.minecraft.util.ResourceLocation;

public interface IRegistryDelegate<T>
{
// leaked by itskekoff; discord.gg/sk3d DoLKYnRP
    T get();

    ResourceLocation name();

    Class<T> type();
}
