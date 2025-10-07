package beame.components.modules.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import events.Event;
import events.impl.player.EventUpdate;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.util.math.vector.Vector3f;
import beame.setting.SettingList.*;

public class SwingAnimations extends Module {
// leaked by itskekoff; discord.gg/sk3d JI7sRNvx
    public SwingAnimations() {
        super("SwingAnimations", Category.Visuals, true, "Модификация анимации удара от 1-го лица");
        addSettings(animate, animationMode, swingPower);
    }

    public final BooleanSetting animate = new BooleanSetting("Анимировать", true, 0);

    public final RadioSetting animationMode = new RadioSetting("Анимация", "Быстрая", "Быстрая", "На боку", "На боку 2", "Штык", "Штык 2").setVisible(animate::get);
    public final SliderSetting swingPower = new SliderSetting("Сила анимации", 5.0f, 1.0f, 10.0f, 0.05f).setVisible(animate::get);

    public void animationProcess(MatrixStack stack, float swingProgress, Runnable runnable) {
        float anim = (float) Math.sin(swingProgress * (Math.PI / 2) * 2);

        if(animate.get()) {
            switch (animationMode.get()) {
                case "Быстрая":
                    runnable.run();
                    break;
                case "На боку":
                    stack.translate(0.4f, 0.1f, -0.5);
                    stack.rotate(Vector3f.YP.rotationDegrees(90));
                    stack.rotate(Vector3f.ZP.rotationDegrees(-60));
                    stack.rotate(Vector3f.XP.rotationDegrees(-90 - (swingPower.get().floatValue() * 10) * anim));
                    break;
                case "На боку 2":
                    stack.translate(0.4f, 0, -0.5f);
                    stack.rotate(Vector3f.YP.rotationDegrees(90));
                    stack.rotate(Vector3f.ZP.rotationDegrees(-30));
                    stack.rotate(Vector3f.XP.rotationDegrees(-90 - (swingPower.get().floatValue() * 10) * anim));
                    break;
                case "Штык":
                    stack.translate(0.2f, 0, -0.2f - ((swingPower.get().floatValue()/2 * 0.1)*anim));
                    stack.rotate(Vector3f.YP.rotationDegrees(0));
                    stack.rotate(Vector3f.XP.rotationDegrees(-90));
                    break;
                case "Штык 2":
                    stack.translate(0, 0.1f, -0.5 - ((swingPower.get().floatValue()/2 * 0.1)*anim));
                    stack.rotate(Vector3f.YP.rotationDegrees(-180));
                    stack.rotate(Vector3f.ZP.rotationDegrees(-60));
                    stack.rotate(Vector3f.YP.rotationDegrees(-20));
                    stack.rotate(Vector3f.XP.rotationDegrees(90 - (swingPower.get().floatValue() * 10)));
                    break;
                default:
                    runnable.run();
                    break;
            }
        }
    }

    @Override
    public void event(Event event) {
        if(event instanceof EventUpdate) {

        }
    }
}
