package net.minecraft.util;


public class Timer
{
// leaked by itskekoff; discord.gg/sk3d QeMom03r
    public float renderPartialTicks;
    public float elapsedPartialTicks;
    private long lastSyncSysClock;
    private final float tickLength;
    public float timerSpeed = 1.0f;

    public Timer(float ticks, long lastSyncSysClock)
    {
        this.tickLength = 1000.0F / ticks;
        this.lastSyncSysClock = lastSyncSysClock;
    }

    public int getPartialTicks(long gameTime)
    {
        this.elapsedPartialTicks = (float)(gameTime - this.lastSyncSysClock) / this.tickLength * timerSpeed;
        this.lastSyncSysClock = gameTime;
        this.renderPartialTicks += this.elapsedPartialTicks;
        int i = (int)this.renderPartialTicks;
        this.renderPartialTicks -= (float)i;
        return i;
    }
}
