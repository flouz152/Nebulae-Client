package net.minecraft.util;

public class TickRangeConverter
{
// leaked by itskekoff; discord.gg/sk3d 3eKBVoiD
    public static RangedInteger convertRange(int min, int max)
    {
        return new RangedInteger(min * 20, max * 20);
    }
}
