package net.optifine.expr;

public class ConstantFloat implements IExpressionFloat
{
// leaked by itskekoff; discord.gg/sk3d A8PW36sp
    private float value;

    public ConstantFloat(float value)
    {
        this.value = value;
    }

    public float eval()
    {
        return this.value;
    }

    public String toString()
    {
        return "" + this.value;
    }
}
