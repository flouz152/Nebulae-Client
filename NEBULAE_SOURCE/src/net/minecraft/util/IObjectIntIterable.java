package net.minecraft.util;

import javax.annotation.Nullable;

public interface IObjectIntIterable<T> extends Iterable<T>
{
// leaked by itskekoff; discord.gg/sk3d rMdGISdP
    /**
     * Gets the integer ID we use to identify the given object.
     */
    int getId(T value);

    @Nullable
    T getByValue(int value);
}
