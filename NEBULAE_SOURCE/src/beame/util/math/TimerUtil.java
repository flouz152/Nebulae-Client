package beame.util.math;

public class TimerUtil {
// leaked by itskekoff; discord.gg/sk3d hTNisDRS
    public long lastMS = System.currentTimeMillis();


    public void reset() {
        lastMS = System.currentTimeMillis();
    }

    public boolean hasTimeElapsed(long time, boolean reset) {
        if (System.currentTimeMillis() - lastMS > time) {
            if (reset) reset();
            return true;
        }

        return false;
    }

    public boolean hasReached(double milliseconds) {
        return getTimePassed() >= milliseconds;
    }
    public long getTimePassed() {
        return System.currentTimeMillis() - lastMS;
    }
    public long setTimePassed(int delay) {
        return System.currentTimeMillis() + delay;
    }

    public long getLastMS() {
        return this.lastMS;
    }

    public void setLastMC() {
        lastMS = System.currentTimeMillis();
    }

    public boolean hasTimeElapsed(long time) {
        return System.currentTimeMillis() - lastMS > time;
    }

    public void setMs(long ms) {
        this.lastMS = System.currentTimeMillis() - ms;
    }

    public boolean finished(long delay) {
        return System.currentTimeMillis() - this.lastMS >= delay;
    }

    public boolean finished(final double delay) {
        return System.currentTimeMillis() - delay >= lastMS;
    }

    public boolean every(long delay) {
        if (System.currentTimeMillis() - this.lastMS >= delay) {
            this.reset();
            return true;
        }
        return false;
    }

    public long getTime() {
        return System.currentTimeMillis() - lastMS;
    }

    public boolean hasTimeElapsed() {
        return lastMS < System.currentTimeMillis();
    }

    public void setTime(long time) {
        lastMS = time;
    }

    public boolean passed(long ms) {
        return System.currentTimeMillis() - lastMS >= ms;
    }

}
