package net.minecraft.entity;

public interface IJumpingMount
{
// leaked by itskekoff; discord.gg/sk3d GiSSJffI
    void setJumpPower(int jumpPowerIn);

    boolean canJump();

    void handleStartJump(int jumpPower);

    void handleStopJump();
}
