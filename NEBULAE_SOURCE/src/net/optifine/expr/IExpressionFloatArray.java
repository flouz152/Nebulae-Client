package net.optifine.expr;

public interface IExpressionFloatArray extends IExpression
{
// leaked by itskekoff; discord.gg/sk3d u1iu1aHA
    float[] eval();

default ExpressionType getExpressionType()
    {
        return ExpressionType.FLOAT_ARRAY;
    }
}
