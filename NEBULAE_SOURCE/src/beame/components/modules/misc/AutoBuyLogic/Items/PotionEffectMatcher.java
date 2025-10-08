package beame.components.modules.misc.AutoBuyLogic.Items;

public class PotionEffectMatcher {
// leaked by itskekoff; discord.gg/sk3d OwC4xbpc
    public final int id;
    public final int amplifier;
    public final int duration; // в секундах, -1 чтобы игнорировать

    public PotionEffectMatcher(int id, int amplifier, int duration) {
        this.id = id;
        this.amplifier = amplifier;
        this.duration = duration;
    }

    public PotionEffectMatcher(int id, int amplifier) {
        this(id, amplifier, -1);
    }
} 