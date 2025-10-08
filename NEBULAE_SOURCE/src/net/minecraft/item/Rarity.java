package net.minecraft.item;

import net.minecraft.util.text.TextFormatting;

public enum Rarity
{
// leaked by itskekoff; discord.gg/sk3d hudgmE5e
    COMMON(TextFormatting.WHITE),
    UNCOMMON(TextFormatting.YELLOW),
    RARE(TextFormatting.AQUA),
    EPIC(TextFormatting.LIGHT_PURPLE);

    public final TextFormatting color;

    private Rarity(TextFormatting p_i48837_3_)
    {
        this.color = p_i48837_3_;
    }
}
