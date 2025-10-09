package net.optifine.expr;

public interface IExpressionFloat extends IExpression
{
// leaked by itskekoff; discord.gg/sk3d dI4XRuA0
    float eval();

default ExpressionType getExpressionType()
    {
        return ExpressionType.FLOAT;
    }
}
