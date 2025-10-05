package net.minecraft.util;

import net.minecraft.util.math.vector.Vector2f;

public class MovementInput
{
// leaked by itskekoff; discord.gg/sk3d WiFYoyY4
    public float moveStrafe;
    public float moveForward;
    public boolean forwardKeyDown;
    public boolean backKeyDown;
    public boolean leftKeyDown;
    public boolean rightKeyDown;
    public boolean jump;
    public boolean sneaking;


    public void tickMovement(boolean p_225607_1_)
    {
    }

    public Vector2f getMoveVector()
    {
        return new Vector2f(this.moveStrafe, this.moveForward);
    }

    public boolean isMovingForward()
    {
        return this.moveForward > 1.0E-5F;
    }
}
