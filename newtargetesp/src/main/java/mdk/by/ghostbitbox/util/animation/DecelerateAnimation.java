package mdk.by.ghostbitbox.util.animation;

public class DecelerateAnimation {

    private final int duration;
    private final double endPoint;
    private Direction direction = Direction.FORWARDS;
    private long startTime = System.currentTimeMillis();

    public DecelerateAnimation(int duration, double endPoint) {
        this.duration = duration;
        this.endPoint = endPoint;
    }

    public void setDirection(Direction direction) {
        if (this.direction != direction) {
            long elapsed = Math.min(duration, Math.max(0L, getElapsed()));
            this.direction = direction;
            startTime = System.currentTimeMillis() - (duration - elapsed);
        }
    }

    public double getOutput() {
        long elapsed = Math.min(duration, Math.max(0L, getElapsed()));
        double normalized = elapsed / (double) duration;
        double eased = 1.0d - Math.pow(normalized - 1.0d, 2.0d);

        if (direction == Direction.FORWARDS) {
            if (elapsed >= duration) {
                return endPoint;
            }
            return eased * endPoint;
        }

        if (elapsed >= duration) {
            return 0.0d;
        }

        double reverse = (duration - elapsed) / (double) duration;
        double reverseEased = 1.0d - Math.pow(reverse - 1.0d, 2.0d);
        return reverseEased * endPoint;
    }

    public void reset() {
        startTime = System.currentTimeMillis();
        direction = Direction.FORWARDS;
    }

    private long getElapsed() {
        return System.currentTimeMillis() - startTime;
    }
}
