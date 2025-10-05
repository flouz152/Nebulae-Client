package beame.util.animation.impl;

import beame.util.animation.Animation;
import beame.util.animation.Direction;

public class EaseInOutQuad extends Animation {
// leaked by itskekoff; discord.gg/sk3d PzKETVIM

    public EaseInOutQuad(int ms, double endPoint) {
        super(ms, endPoint);
    }

    public EaseInOutQuad(int ms, double endPoint, Direction direction) {
        super(ms, endPoint, direction);
    }

    protected double getEquation(double x1) {
        double x = x1 / duration;
        return x < 0.5 ? 2 * Math.pow(x, 2) : 1 - Math.pow(-2 * x + 2, 2) / 2;
    }

}
