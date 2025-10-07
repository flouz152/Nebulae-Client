package beame.feature.ui;

import beame.Nebulae;
import beame.util.animation.AnimationMath;
import beame.util.color.ColorUtils;
import beame.util.drag.Dragging;
import beame.util.fonts.Fonts;
import beame.util.math.TimerUtil;
import beame.util.other.HudUtil;
import beame.util.render.ClientHandler;

import static beame.util.IMinecraft.mc;

public class InfoDraw {
// leaked by itskekoff; discord.gg/sk3d gWxUijK7
    public Dragging InfoDrag = Nebulae.getHandler().createDraggable("info", 5, mc.getMainWindow().getScaledHeight() - 35);
    
    float bps_width = 50;
    float coords_width = 50;

    float maxBps = 0;
    TimerUtil resetMax = new TimerUtil();
    float interpolatedMaxBps = 0;

    public void render(boolean hideCoords) {
        float x = InfoDrag.getX();
        float y = InfoDrag.getY() + 30; // Смещение для правильного позиционирования

        float bps_val = (float)HudUtil.calculateBPS();
        maxBps = Math.max(bps_val, maxBps);
        if(bps_val > maxBps) resetMax.reset();
        String maxBpsText = (maxBps > bps_val / 2) ? " (max: " + String.format("%.2f", maxBps) + " bps)" : "";
        String bps = String.format("%.2f", bps_val) + " bps" + maxBpsText;

        if(resetMax.hasReached(2500)) {
            maxBps = 0;
            resetMax.reset();
        }

        String coords = String.format("%.0f", mc.player.getPosX()).replace(",", ".") + ", " + String.format("%.0f", mc.player.getPosY()).replace(",", ".") + ", " + String.format("%.0f", mc.player.getPosZ()).replace(",", ".");
        // if(hideCoords) coords = "?, ?, ?";

        bps_width = AnimationMath.fast(bps_width, Fonts.SF_BOLD.get(14).getStringWidth(bps) + 21, 8);
        coords_width = AnimationMath.fast(coords_width, Fonts.SF_BOLD.get(14).getStringWidth(coords) + 21, 8);

        renderPart("p", bps, y - 15, 0, 0, bps_width, 15);
        if (!hideCoords) {
            renderPart("j", coords, y - 15, (bps_width + 3), 0, coords_width, 15);
        }
    }

    private void renderPart(String icon, String text, float pos, float x, float y, float width, float height) {
        ClientHandler.drawSexyRect(InfoDrag.getX() + x, pos + y, width, height, Nebulae.getHandler().getModuleList().hud.rounding.get(), false);
        Fonts.ESSENCE_ICONS.get(18).drawString(icon, InfoDrag.getX() + x + 4, pos + y + 2 + 4, Nebulae.getHandler().themeManager.getThemeColor(0));
        Fonts.SF_BOLD.get(14).drawString(text, InfoDrag.getX() + x + 15, pos + y + 2 + 4f, ColorUtils.getColor(120));
    }
}
