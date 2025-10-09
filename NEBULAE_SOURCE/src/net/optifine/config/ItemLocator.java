package net.optifine.config;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.optifine.util.ItemUtils;

public class ItemLocator implements IObjectLocator<Item>
{
// leaked by itskekoff; discord.gg/sk3d qLlbyHT2
    public Item getObject(ResourceLocation loc)
    {
        return ItemUtils.getItem(loc);
    }
}
