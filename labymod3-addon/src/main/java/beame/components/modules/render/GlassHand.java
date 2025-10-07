package beame.components.modules.render;

import beame.Essence;
import beame.util.color.ColorUtils;
import beame.util.render.CustomFramebuffer;
import beame.util.render.KawaseBlur;
import com.mojang.blaze3d.systems.RenderSystem;
import events.Event;
import events.impl.render.EventRender;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.client.settings.PointOfView;
import beame.setting.SettingList.RadioSetting;

public class GlassHand extends Module {
// leaked by itskekoff; discord.gg/sk3d pQq9UhNS

    private final RadioSetting ccType = new RadioSetting("Режим", "Бесцветный", "Бесцветный", "Тема");
    public CustomFramebuffer customFramebuffer = new CustomFramebuffer(false);

    public GlassHand() {
        super("GlassHand", Category.Visuals, true, "Делает руки стеклянными.");
        addSettings(ccType);
    }

    @Override
    public void event(Event event) {
        if (event instanceof EventRender eventRender) {
            if (eventRender.isGlassHand() && mc.gameSettings.getPointOfView() == PointOfView.FIRST_PERSON) {
                RenderSystem.pushMatrix();
                if (ccType.is("Бесцветный")) {
                    ColorUtils.setColor(ColorUtils.rgba(128, 128, 128, 255));
                } else {
                    Essence.getHandler().getThemeManager().getThemeColor(0);
                    Essence.getHandler().getThemeManager().getThemeColor(1);
                }
                KawaseBlur.applyBlur(() -> customFramebuffer.draw(), 3, 3);
                RenderSystem.color4f(1, 1, 1, 1);
                RenderSystem.popMatrix();
            }
        }
    }

}
