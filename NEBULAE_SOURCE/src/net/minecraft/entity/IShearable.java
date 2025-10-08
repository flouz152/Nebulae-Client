package net.minecraft.entity;

import net.minecraft.util.SoundCategory;

public interface IShearable
{
// leaked by itskekoff; discord.gg/sk3d ZGc3tJLs
    void shear(SoundCategory category);

    boolean isShearable();
}
