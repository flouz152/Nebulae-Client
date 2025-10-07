package beame.components.baritone.api.utils;

/**
 * @author Brady
 */
public enum BooleanBinaryOperators implements BooleanBinaryOperator {
// leaked by itskekoff; discord.gg/sk3d HSGrcSqP
    OR((a, b) -> a || b),
    AND((a, b) -> a && b),
    XOR((a, b) -> a ^ b);

    private final BooleanBinaryOperator op;

    BooleanBinaryOperators(BooleanBinaryOperator op) {
        this.op = op;
    }

    @Override
    public boolean applyAsBoolean(boolean a, boolean b) {
        return this.op.applyAsBoolean(a, b);
    }
}
