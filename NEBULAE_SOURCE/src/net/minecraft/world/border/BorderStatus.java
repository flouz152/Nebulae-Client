package net.minecraft.world.border;

public enum BorderStatus
{
// leaked by itskekoff; discord.gg/sk3d 2iHjFbhd
    GROWING(4259712),
    SHRINKING(16724016),
    STATIONARY(2138367);

    private final int color;

    private BorderStatus(int color)
    {
        this.color = color;
    }

    /**
     * Retrieve the color that the border should be while in this state
     */
    public int getColor()
    {
        return this.color;
    }
}
