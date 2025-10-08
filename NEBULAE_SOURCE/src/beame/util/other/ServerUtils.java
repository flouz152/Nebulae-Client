package beame.util.other;

import beame.Nebulae;
import com.google.common.eventbus.Subscribe;
import events.impl.packet.EventPacket;
import lombok.Getter;
import net.minecraft.network.play.server.SUpdateTimePacket;
import net.minecraft.util.math.MathHelper;
import java.util.Arrays;

public class ServerUtils {
// leaked by itskekoff; discord.gg/sk3d zMtf0rGy
    protected final float[] ticks = new float[20];
    protected int index;
    protected long lastPacketTime;

    @Getter
    private float TPS = 20;
    @Getter
    private float adjustTicks = 0;
    private long timestamp;

    private void initialize() {
        this.index = 0;
        this.lastPacketTime = -1L;
        Arrays.fill(ticks, 0.0F);

        Nebulae.getHandler().getEventBus().register(this);
    }

    public ServerUtils() {
        initialize();
    }

    public float getAverageTPS() {
        float numTicks = 0.0F;
        float sumTickRates = 0.0F;
        for (float tickRate : ticks) {
            if (tickRate > 0.0F) {
                sumTickRates += tickRate;
                numTicks++;
            }
        }
        return numTicks > 0 ? MathHelper.clamp(sumTickRates / numTicks, 0.0F, 20.0F) : 20.0F;
    }

    private void update() {
        if (this.lastPacketTime != -1L) {
            float timeElapsed = (float) (System.currentTimeMillis() - this.lastPacketTime) / 1000.0F;
            ticks[this.index % ticks.length] = MathHelper.clamp(20.0F / timeElapsed, 0.0F, 20.0F);
            this.index++;
        }
        this.lastPacketTime = System.currentTimeMillis();
    }

    @Subscribe
    public void onPacket(EventPacket e) {
        update();

        if (e.getPacket() instanceof SUpdateTimePacket) {
            updateTPS();
        }
    }

    private void updateTPS() {
        long delay = System.nanoTime() - timestamp;
        if (delay == 0) return;

        float maxTPS = 20.0F;
        float rawTPS = maxTPS * (1e9f / delay);

        float boundedTPS = MathHelper.clamp(rawTPS, 0.0F, maxTPS);

        TPS = (float) round(boundedTPS);
        adjustTicks = boundedTPS - maxTPS;

        timestamp = System.nanoTime();
    }

    public double round(final double input) {
        return Math.round(input * 100.0) / 100.0;
    }
}
