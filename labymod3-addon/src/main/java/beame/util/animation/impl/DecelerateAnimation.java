package beame.util.animation.impl;

import beame.util.animation.Animation;
import beame.util.animation.Direction;

public class DecelerateAnimation extends Animation {
// leaked by itskekoff; discord.gg/sk3d M7asdRe0

    public DecelerateAnimation(int ms, double endPoint) {
        super(ms, endPoint);
    }

    public DecelerateAnimation(int ms, double endPoint, Direction direction) {
        super(ms, endPoint, direction);
    }

    protected double getEquation(double x) {
        double x1 = x / duration;
        return 1 - ((x1 - 1) * (x1 - 1));
    }
}
