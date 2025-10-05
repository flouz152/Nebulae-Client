package net.minecraft.util;

import java.util.function.Supplier;

public class LazyValue<T>
{
// leaked by itskekoff; discord.gg/sk3d lBgzdJz7
    private Supplier<T> supplier;
    private T value;

    public LazyValue(Supplier<T> supplierIn)
    {
        this.supplier = supplierIn;
    }

    public T getValue()
    {
        Supplier<T> supplier = this.supplier;

        if (supplier != null)
        {
            this.value = supplier.get();
            this.supplier = null;
        }

        return this.value;
    }
}
