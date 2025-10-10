package beame.labyaddon.util.animation.impl;

import beame.labyaddon.util.animation.Animation;

public class DecelerateAnimation extends Animation {

    public DecelerateAnimation(int durationMs, double endPoint) {
        super(durationMs, endPoint);
    }

    @Override
    protected double getEquation(double time) {
        double x = Math.min(1.0D, Math.max(0.0D, time / duration));
        return 1.0D - Math.pow(x - 1.0D, 2.0D);
    }
}
