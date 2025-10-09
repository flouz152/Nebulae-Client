package net.minecraft.dispenser;

public abstract class OptionalDispenseBehavior extends DefaultDispenseItemBehavior
{
// leaked by itskekoff; discord.gg/sk3d sAhuyoeU
    private boolean successful = true;

    public boolean isSuccessful()
    {
        return this.successful;
    }

    public void setSuccessful(boolean success)
    {
        this.successful = success;
    }

    /**
     * Play the dispense sound from the specified block.
     */
    protected void playDispenseSound(IBlockSource source)
    {
        source.getWorld().playEvent(this.isSuccessful() ? 1000 : 1001, source.getBlockPos(), 0);
    }
}
