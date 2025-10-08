package net.optifine.expr;

public interface IExpressionBool extends IExpression
{
// leaked by itskekoff; discord.gg/sk3d oOMg6fwP
    boolean eval();

default ExpressionType getExpressionType()
    {
        return ExpressionType.BOOL;
    }
}
