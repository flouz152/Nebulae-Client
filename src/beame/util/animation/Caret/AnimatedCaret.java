package beame.util.animation.Caret;

import beame.Essence;
import beame.util.color.ColorUtils;
import beame.util.fonts.Fonts;
import beame.util.render.ClientHandler;

public class AnimatedCaret {
// leaked by itskekoff; discord.gg/sk3d Ec7qeHCO
    private float animatedX = 0;

    public void draw(float x, float y, String searchText) {
        long currentTime = System.currentTimeMillis();
        int alpha = (int) (128 + 127 * Math.sin(currentTime / 170.0));
        float targetX = x + Fonts.SUISSEINTL.get(14).getStringWidth(searchText) - 0.5f;

        if (Math.abs(animatedX - targetX) > 0.1f) {
            animatedX += (targetX - animatedX) * 0.1f;
        } else {
            animatedX = targetX;
        }

        ClientHandler.drawRound(animatedX, y, 1, 10, 3, ColorUtils.rgba(Essence.getHandler().styler.clr_main, Essence.getHandler().styler.clr_main, Essence.getHandler().styler.clr_main, alpha));
    }
}
