package beame.feature.themes;

import beame.util.animation.AnimationMath;

public class StyleManager {
// leaked by itskekoff; discord.gg/sk3d Rr2J7JT5
    public int Style = 0;

    public int clr_main = 255;
    public int clr17 = 17;
    public int clr24 = 24;
    public int clr20 = 20;
    public int clr29 = 29;
    public int clr180 = 180;
    public int clr120 = 120;
    public int clr30 = 30;
    public int clr160 = 160;
    public int clr25 = 25;
    public int clr190 = 190;

    public void update() {
        clr_main = (int)AnimationMath.fast(clr_main, Style == 0 ? 255 : 0, 10);
        clr17 = (int)AnimationMath.fast(clr17, Style == 0 ? 17 : 255-(17*1.5f), 10);
        clr24 = (int)AnimationMath.fast(clr24, Style == 0 ? 24 : 255-(24*1.5f), 10);
        clr29 = (int)AnimationMath.fast(clr29, Style == 0 ? 29 : 255-(29*1.5f), 10);
        clr180 = (int)AnimationMath.fast(clr180, Style == 0 ? 180 : 255-(120*1.5f), 10);
        clr120 = (int)AnimationMath.fast(clr120, Style == 0 ? 120 : 255-(90*1.5f), 10);
        clr20 = (int)AnimationMath.fast(clr20, Style == 0 ? 20 : 255-(20*1.5f), 10);
        clr30 = (int)AnimationMath.fast(clr30, Style == 0 ? 30 : 255-(30*1.5f), 10);
        clr160 = (int)AnimationMath.fast(clr160, Style == 0 ? 160 : 255-(105*1.5f), 10);
        clr25 = (int)AnimationMath.fast(clr25, Style == 0 ? 25 : 255-(25*1.5f), 10);
        clr190 = (int)AnimationMath.fast(clr190, Style == 0 ? 190 : 255-(125*1.5f), 10);
    }
}
