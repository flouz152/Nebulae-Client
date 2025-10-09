package beame.wavecapes.config;


import beame.wavecapes.CapeMovement;
import beame.wavecapes.CapeStyle;
import beame.wavecapes.WindMode;

public class Config {
// leaked by itskekoff; discord.gg/sk3d EDOGt1cy

    public int configVersion = 2;
    public WindMode windMode = WindMode.WAVES;
    public CapeStyle capeStyle = CapeStyle.SMOOTH;
    public CapeMovement capeMovement = CapeMovement.BASIC_SIMULATION;
    //public int capeParts = 16;
    public int gravity = 30;
    public int heightMultiplier = 4;
    public int straveMultiplier = 2;
    //public int maxBend = 5;
}
