package nebulae.addon.gui;

import beame.module.Module;
import beame.setting.ConfigSetting;
import beame.setting.SettingList.BindSetting;
import beame.setting.SettingList.BooleanSetting;
import beame.setting.SettingList.EnumSetting;
import beame.setting.SettingList.RadioSetting;
import beame.setting.SettingList.SliderSetting;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

public class ModuleScreen extends Screen {

    private final Module module;
    private final Map<Button, BooleanSetting> booleanButtons = new HashMap<>();
    private final Map<Button, SliderSetting> sliderIncreaseButtons = new HashMap<>();
    private final Map<Button, SliderSetting> sliderDecreaseButtons = new HashMap<>();
    private final Map<Button, SliderSetting> sliderLabelButtons = new HashMap<>();
    private final Map<Button, BindSetting> bindButtons = new HashMap<>();

    private BindSetting listeningForBind;

    public ModuleScreen(Module module) {
        super(new StringTextComponent(module.getName()));
        this.module = module;
    }

    @Override
    protected void init() {
        super.init();
        this.buttons.clear();
        this.children.clear();
        this.booleanButtons.clear();
        this.sliderIncreaseButtons.clear();
        this.sliderDecreaseButtons.clear();
        this.sliderLabelButtons.clear();
        this.bindButtons.clear();

        int centerX = this.width / 2;
        int y = this.height / 4;

        Button toggleButton = this.addButton(new Button(centerX - 100, y, 200, 20, getToggleLabel(), (btn) -> {
            module.toggle();
            btn.setMessage(getToggleLabel());
        }));
        toggleButton.active = true;

        y += 30;

        for (ConfigSetting<?> setting : module.getConfigSettings()) {
            if (!setting.visible.get()) {
                continue;
            }

            if (setting instanceof BooleanSetting) {
                BooleanSetting booleanSetting = (BooleanSetting) setting;
                Button button = this.addButton(new Button(centerX - 100, y, 200, 20, booleanLabel(booleanSetting), btn -> {
                    booleanSetting.set(!booleanSetting.get());
                    btn.setMessage(booleanLabel(booleanSetting));
                }));
                booleanButtons.put(button, booleanSetting);
                y += 24;
            } else if (setting instanceof SliderSetting) {
                SliderSetting slider = (SliderSetting) setting;
                Button decrease = this.addButton(new Button(centerX - 100, y, 40, 20, new StringTextComponent("-"), btn -> {
                    slider.set(Math.max(slider.min, slider.get() - slider.increment));
                    refreshSliderButtons();
                }));
                Button label = this.addButton(new Button(centerX - 60, y, 120, 20, sliderLabel(slider), btn -> {
                    slider.set(slider.min);
                    refreshSliderButtons();
                }));
                label.active = false;
                Button increase = this.addButton(new Button(centerX + 60, y, 40, 20, new StringTextComponent("+"), btn -> {
                    slider.set(Math.min(slider.max, slider.get() + slider.increment));
                    refreshSliderButtons();
                }));
                sliderDecreaseButtons.put(decrease, slider);
                sliderIncreaseButtons.put(increase, slider);
                sliderLabelButtons.put(label, slider);
                y += 24;
            } else if (setting instanceof RadioSetting) {
                RadioSetting radio = (RadioSetting) setting;
                Button button = this.addButton(new Button(centerX - 100, y, 200, 20, radioLabel(radio), btn -> {
                    int next = (radio.getIndex() + 1) % radio.strings.length;
                    radio.set(radio.strings[next]);
                    btn.setMessage(radioLabel(radio));
                }));
                y += 24;
            } else if (setting instanceof EnumSetting) {
                EnumSetting enumSetting = (EnumSetting) setting;
                for (BooleanSetting option : enumSetting.get()) {
                    Button button = this.addButton(new Button(centerX - 100, y, 200, 20, booleanLabel(option), btn -> {
                        option.set(!option.get());
                        btn.setMessage(booleanLabel(option));
                    }));
                    booleanButtons.put(button, option);
                    y += 24;
                }
            } else if (setting instanceof BindSetting) {
                BindSetting bind = (BindSetting) setting;
                Button button = this.addButton(new Button(centerX - 100, y, 200, 20, bindLabel(bind), btn -> {
                    listeningForBind = bind;
                    btn.setMessage(new StringTextComponent("Press a key..."));
                }));
                bindButtons.put(button, bind);
                y += 24;
            }
        }

        this.addButton(new Button(centerX - 100, this.height - 30, 200, 20, new StringTextComponent("Done"), btn -> {
            Minecraft.getInstance().displayGuiScreen(null);
        }));
    }

    private ITextComponent getToggleLabel() {
        return new StringTextComponent(module.getName() + ": " + (module.isState() ? "Disable" : "Enable"));
    }

    private ITextComponent booleanLabel(BooleanSetting setting) {
        return new StringTextComponent(setting.getName() + ": " + (setting.get() ? "ON" : "OFF"));
    }

    private ITextComponent radioLabel(RadioSetting setting) {
        return new StringTextComponent(setting.getName() + ": " + setting.get());
    }

    private ITextComponent sliderLabel(SliderSetting setting) {
        return new StringTextComponent(setting.getName() + ": "" + String.format("%.2f", setting.get()));
    }

    private ITextComponent bindLabel(BindSetting setting) {
        int key = setting.get();
        String text = key == 0 ? "Unassigned" : GLFW.glfwGetKeyName(key, 0);
        if (text == null || text.isEmpty()) {
            text = "Key " + key;
        }
        return new StringTextComponent(setting.getName() + ": " + text);
    }

    private void refreshSliderButtons() {
        for (Map.Entry<Button, SliderSetting> entry : sliderIncreaseButtons.entrySet()) {
            entry.getKey().setMessage(new StringTextComponent("+"));
        }
        for (Map.Entry<Button, SliderSetting> entry : sliderDecreaseButtons.entrySet()) {
            entry.getKey().setMessage(new StringTextComponent("-"));
        }
        for (Map.Entry<Button, SliderSetting> entry : sliderLabelButtons.entrySet()) {
            entry.getKey().setMessage(sliderLabel(entry.getValue()));
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (listeningForBind != null) {
            listeningForBind.set(keyCode);
            refreshBindButtons();
            listeningForBind = null;
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void refreshBindButtons() {
        for (Map.Entry<Button, BindSetting> entry : bindButtons.entrySet()) {
            entry.getKey().setMessage(bindLabel(entry.getValue()));
        }
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        drawCenteredString(stack, this.font, this.title.getString(), this.width / 2, 20, 0xFFFFFF);
        super.render(stack, mouseX, mouseY, partialTicks);
    }
}
