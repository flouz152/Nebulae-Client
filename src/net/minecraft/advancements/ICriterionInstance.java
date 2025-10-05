package net.minecraft.advancements;

import com.google.gson.JsonObject;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.ResourceLocation;

public interface ICriterionInstance
{
// leaked by itskekoff; discord.gg/sk3d llfI5AZw
    ResourceLocation getId();

    JsonObject serialize(ConditionArraySerializer conditions);
}
