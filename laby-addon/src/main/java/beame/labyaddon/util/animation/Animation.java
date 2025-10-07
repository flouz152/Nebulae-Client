package beame.labyaddon.util.animation;

import beame.labyaddon.util.math.TimerUtil;

public abstract class Animation {

    protected final TimerUtil timer = new TimerUtil();
    protected int duration;
    protected double endPoint;
    protected Direction direction;

    protected Animation(int durationMs, double endPoint) {
        this(durationMs, endPoint, Direction.FORWARDS);
    }

    protected Animation(int durationMs, double endPoint, Direction direction) {
        this.duration = durationMs;
        this.endPoint = endPoint;
        this.direction = direction;
    }

    public void setDirection(Direction direction) {
        if (this.direction != direction) {
            this.direction = direction;
            long elapsed = Math.min(duration, timer.getTime());
            timer.setTime(System.currentTimeMillis() - (duration - elapsed));
        }
    }

    public Direction getDirection() {
        return direction;
    }

    public double getOutput() {
        double progress = Math.min(1.0D, Math.max(0.0D, timer.getTime() / (double) duration));
        double equation = getEquation(progress * duration);
        if (direction == Direction.FORWARDS) {
            return equation * endPoint;
        }

        if (isFinished()) {
            return 0.0D;
        }

        return (1.0D - equation) * endPoint;
    }

    public boolean isFinished() {
        return timer.getTime() >= duration;
    }

    protected abstract double getEquation(double time);
}
