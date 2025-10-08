package net.minecraftforge.fml.common.registry;

import net.minecraft.util.ResourceLocation;

public interface RegistryDelegate<T>
{
// leaked by itskekoff; discord.gg/sk3d laToVYiB
    T get();

    ResourceLocation name();

    Class<T> type();
}
