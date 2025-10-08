package net.minecraft.util;

import javax.annotation.Nullable;
import net.minecraft.util.text.ITextComponent;

public interface INameable
{
// leaked by itskekoff; discord.gg/sk3d G8SFfN4r
    ITextComponent getName();

default boolean hasCustomName()
    {
        return this.getCustomName() != null;
    }

default ITextComponent getDisplayName()
    {
        return this.getName();
    }

    @Nullable

default ITextComponent getCustomName()
    {
        return null;
    }
}
