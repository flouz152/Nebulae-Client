package net.minecraft.loot;

import net.minecraft.loot.conditions.ILootCondition;

public interface ILootConditionConsumer<T>
{
// leaked by itskekoff; discord.gg/sk3d HnkDMHn7
    T acceptCondition(ILootCondition.IBuilder conditionBuilder);

    T cast();
}
