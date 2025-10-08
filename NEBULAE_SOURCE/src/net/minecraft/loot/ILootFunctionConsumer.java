package net.minecraft.loot;

import net.minecraft.loot.functions.ILootFunction;

public interface ILootFunctionConsumer<T>
{
// leaked by itskekoff; discord.gg/sk3d 2zF70tcm
    T acceptFunction(ILootFunction.IBuilder functionBuilder);

    T cast();
}
