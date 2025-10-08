package beame.feature.ui;

import beame.Nebulae;
import beame.util.animation.AnimationMath;
import beame.util.animation.NumberTransition;
import beame.util.color.ColorUtils;
import beame.util.fonts.Fonts;
import beame.util.other.HudUtil;
import beame.util.render.ClientHandler;

import static beame.util.IMinecraft.mc;

public class WaterMarkDrawOLD {
// leaked by itskekoff; discord.gg/sk3d nSEiIKhb
    float title_width = 50;
    float username_width = 100;
    float server_width = 100;
    float fps_width = 50;
    float ping_width = 50;

    float tempFps = 0;
    float tempPing = 0;

    public void render(boolean ghost) {
        float pos = 8;
        String username = Nebulae.getHandler().getUserName();
        String title = "Nebulae";
        String server = HudUtil.serverIP();
        tempFps = NumberTransition.result(tempFps, mc.debugFPS);
        String fps = ((int) tempFps) + " fps";
        tempPing = NumberTransition.result(tempPing, HudUtil.calculatePing());
        String ping = ((int) tempPing) + " ms";
        username = username.length() > 12 ? username.substring(0, 12) : username;
        title_width = AnimationMath.fast(title_width, Fonts.SF_BOLD.get(14).getStringWidth(title) + Fonts.SF_BOLD.get(14).getStringWidth(username) , 8);
        username_width = AnimationMath.fast(username_width, Fonts.SF_BOLD.get(14).getStringWidth(username) - 5, 8);
        server_width = AnimationMath.fast(server_width, Fonts.SF_BOLD.get(14).getStringWidth(server) + 21, 8);
        fps_width = AnimationMath.fast(fps_width, Fonts.SF_BOLD.get(14).getStringWidth(fps) + 21, 8);
        ping_width = AnimationMath.fast(ping_width, Fonts.SF_BOLD.get(14).getStringWidth(ping) + 21, 8);


        ClientHandler.drawSexyRect(pos, pos, title_width + 33, 15, Nebulae.getHandler().getModuleList().hud.rounding.get(), true);

        Fonts.SF_BOLD.get(39).drawString("/", pos  - 22.5F + 58, pos - 0.7F, ColorUtils.setAlpha(ColorUtils.getColor(60), 90)); // палка 1
        Fonts.SF_BOLD.get(39).drawString("/", pos  - 19.5F + 58, pos - 0.7F, ColorUtils.setAlpha(ColorUtils.getColor(60), 90)); // палка 2

        if (ghost)
            Fonts.LOGO.get(18).drawString("f", pos + 3, pos + 2 + 4.5f, Nebulae.getHandler().themeManager.getThemeColor(0));
        Fonts.SF_BOLD.get(15).drawGradientString(title, pos + 5, pos + 2 + 4, Nebulae.getHandler().themeManager.getThemeColor(0), Nebulae.getHandler().themeManager.getColor(180), 33, 15);
        Fonts.ICONS2.get(18).drawString("d", pos  - 13 + 58, pos + 2 + 4, ColorUtils.getColor(120)); // иконка
        Fonts.SUISSEINTL.get(15).drawString(username, pos  - 3 + 58, pos + 2 + 4, ColorUtils.getColor(120)); // ник

    }

    private void renderPart(String icon, String text, float pos, float x, float y, float width, float height) {
        ClientHandler.drawSexyRect(pos + x, pos + y, width, height, 4, false);
        Fonts.ESSENCE_ICONS.get(18).drawString(icon, pos + x + 4, pos + y + 2 + 4, Nebulae.getHandler().themeManager.getThemeColor(0));
        Fonts.SF_BOLD.get(14).drawString(text, pos + x + 15, pos + y + 2 + 4f, ColorUtils.getColor(120));
    }


}
