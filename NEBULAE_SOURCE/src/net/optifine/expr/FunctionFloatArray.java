package net.optifine.expr;

public class FunctionFloatArray implements IExpressionFloatArray
{
// leaked by itskekoff; discord.gg/sk3d vNxljpwO
    private FunctionType type;
    private IExpression[] arguments;

    public FunctionFloatArray(FunctionType type, IExpression[] arguments)
    {
        this.type = type;
        this.arguments = arguments;
    }

    public float[] eval()
    {
        return this.type.evalFloatArray(this.arguments);
    }

    public String toString()
    {
        return "" + this.type + "()";
    }
}
