package beame.labyaddon.util.math;

public class TimerUtil {

    private long lastMs = System.currentTimeMillis();

    public void reset() {
        lastMs = System.currentTimeMillis();
    }

    public long getTime() {
        return System.currentTimeMillis() - lastMs;
    }

    public void setTime(long time) {
        lastMs = time;
    }
}
