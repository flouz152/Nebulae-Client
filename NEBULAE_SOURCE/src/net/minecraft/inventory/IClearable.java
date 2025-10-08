package net.minecraft.inventory;

import javax.annotation.Nullable;

public interface IClearable
{
// leaked by itskekoff; discord.gg/sk3d 2sMNw2jD
    void clear();

    static void clearObj(@Nullable Object object)
    {
        if (object instanceof IClearable)
        {
            ((IClearable)object).clear();
        }
    }
}
