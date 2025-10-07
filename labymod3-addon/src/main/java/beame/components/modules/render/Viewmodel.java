package beame.components.modules.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import events.Event;
import beame.module.Category;
import beame.module.Module;
import beame.setting.SettingList.RadioSetting;
import beame.setting.SettingList.SliderSetting;

public class Viewmodel extends Module {
// leaked by itskekoff; discord.gg/sk3d 41eECucQ
    public Viewmodel() {
        super("Viewmodel", Category.Visuals, true, "Модификация позиционирования рук игрока");
        addSettings(handSelection, right_scale, right_x, right_y, right_z, left_scale, left_x, left_y, left_z);
    }

    public final RadioSetting handSelection = new RadioSetting("Выберите руку для настройки", "Правая", "Правая", "Левая");

    public final SliderSetting right_scale = new SliderSetting("Размер предмета", 1.0F, 0f, 2.0f, 0.1F).setVisible(() -> handSelection.is("Правая"));
    public final SliderSetting right_x = new SliderSetting("Позиция X", 0.0F, -2.0f, 2.0f, 0.1F).setVisible(() -> handSelection.is("Правая"));
    public final SliderSetting right_y = new SliderSetting("Позиция Y", 0.0F, -2.0f, 2.0f, 0.1F).setVisible(() -> handSelection.is("Правая"));
    public final SliderSetting right_z = new SliderSetting("Позиция Z", 0.0F, -2.0f, 2.0f, 0.1F).setVisible(() -> handSelection.is("Правая"));

    public final SliderSetting left_scale = new SliderSetting("Размер предмета ", 1.0F, 0f, 2.0f, 0.1F).setVisible(() -> handSelection.is("Левая"));
    public final SliderSetting left_x = new SliderSetting("Позиция X ", 0.0F, -2.0f, 2.0f, 0.1F).setVisible(() -> handSelection.is("Левая"));
    public final SliderSetting left_y = new SliderSetting("Позиция Y ", 0.0F, -2.0f, 2.0f, 0.1F).setVisible(() -> handSelection.is("Левая"));
    public final SliderSetting left_z = new SliderSetting("Позиция Z ", 0.0F, -2.0f, 2.0f, 0.1F).setVisible(() -> handSelection.is("Левая"));

    @Override
    public void event(Event event) { }

    public void setupRightHand(MatrixStack stack) {
        stack.scale(right_scale.get().floatValue(), right_scale.get().floatValue(), right_scale.get().floatValue());
        stack.translate(right_x.get().floatValue() / 2, right_y.get().floatValue() / 2, right_z.get().floatValue() / 2);
    }
    public void setupLeftHand(MatrixStack stack) {
        stack.scale(left_scale.get().floatValue(), left_scale.get().floatValue(), left_scale.get().floatValue());
        stack.translate(left_x.get().floatValue() / 2, left_y.get().floatValue() / 2, left_z.get().floatValue() / 2);
    }
}
