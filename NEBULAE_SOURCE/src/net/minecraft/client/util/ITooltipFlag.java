package net.minecraft.client.util;

public interface ITooltipFlag
{
// leaked by itskekoff; discord.gg/sk3d vfU07nhD
    boolean isAdvanced();

    public static enum TooltipFlags implements ITooltipFlag
    {
        NORMAL(false),
        ADVANCED(true);

        private final boolean isAdvanced;

        private TooltipFlags(boolean advanced)
        {
            this.isAdvanced = advanced;
        }

        public boolean isAdvanced()
        {
            return this.isAdvanced;
        }
    }
}
