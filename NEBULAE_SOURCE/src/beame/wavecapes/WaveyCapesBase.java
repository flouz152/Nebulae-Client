package beame.wavecapes;

import beame.wavecapes.config.Config;

public class WaveyCapesBase {
// leaked by itskekoff; discord.gg/sk3d x07d3RsL
    public static WaveyCapesBase INSTANCE;
    public static Config config;

    public WaveyCapesBase() {
        INSTANCE = this;
        config = new Config();
    }
}
