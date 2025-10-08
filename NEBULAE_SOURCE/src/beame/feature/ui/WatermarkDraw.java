package beame.feature.ui;

import beame.Nebulae;
import beame.util.animation.AnimationMath;
import beame.util.animation.NumberTransition;
import beame.util.color.ColorUtils;
import beame.util.fonts.Fonts;
import beame.util.other.HudUtil;
import beame.util.render.ClientHandler;
import static beame.util.IMinecraft.mc;

public class WatermarkDraw {
// leaked by itskekoff; discord.gg/sk3d fkdjaJ0C
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

        title_width = AnimationMath.fast(title_width, Fonts.SF_BOLD.get(14).getStringWidth(title) + (ghost ? 21 : 11), 8);
        username_width = AnimationMath.fast(username_width, Fonts.SF_BOLD.get(14).getStringWidth(username) + 21, 8);
        server_width = AnimationMath.fast(server_width, Fonts.SF_BOLD.get(14).getStringWidth(server) + 21, 8);
        fps_width = AnimationMath.fast(fps_width, Fonts.SF_BOLD.get(14).getStringWidth(fps) + 21, 8);
        ping_width = AnimationMath.fast(ping_width, Fonts.SF_BOLD.get(14).getStringWidth(ping) + 21, 8);

        { // main part

            ClientHandler.drawSexyRect(pos, pos, title_width, 15, Nebulae.getHandler().getModuleList().hud.rounding.get(), true);
//
//            Fonts.SF_BOLD.get(39).drawString("/", pos + title_width - 15, pos - 1, ColorUtils.setAlpha(ColorUtils.getColor(60), 90));
//            Fonts.SF_BOLD.get(39).drawString("/", pos + title_width - 12, pos - 1, ColorUtils.setAlpha(ColorUtils.getColor(60), 90));

            if (ghost)
                Fonts.LOGO.get(18).drawString("Z", pos + 3, pos + 2 + 4.5f, Nebulae.getHandler().themeManager.getThemeColor(0));
            Fonts.SF_BOLD.get(14).drawGradientString(title, pos + 5 + (ghost ? 10 : 0), pos + 2 + 4.5f, Nebulae.getHandler().themeManager.getThemeColor(0), Nebulae.getHandler().themeManager.getColor(180), 55, 7);



            renderPart("b", username, pos, (title_width + 3), 0, username_width, 15);
            renderPart("m", fps, pos, (username_width + 3) + (title_width + 3), 0, fps_width, 15);
            renderPart("n", server, pos, 0, (15 + 3), server_width, 15);
            renderPart("l", ping, pos, (server_width + 3), (15 + 3), ping_width, 15);
        }

    }

    private void renderPart(String icon, String text, float pos, float x, float y, float width, float height) {
        int alpha = 255;


        ClientHandler.drawSexyRect(pos + x, pos + y, width, height, Nebulae.getHandler().getModuleList().hud.rounding.get(), false);
        Fonts.ESSENCE_ICONS.get(18).drawString(icon, pos + x + 4, pos + y + 2 + 4, ColorUtils.rgba(60, 60, 60, alpha));
        Fonts.SF_BOLD.get(14).drawString(text, pos + x + 15, pos + y + 2 + 4f, ColorUtils.getColor(120));
    }


}
