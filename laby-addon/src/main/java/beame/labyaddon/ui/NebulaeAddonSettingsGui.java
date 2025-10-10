package beame.labyaddon.ui;

import beame.labyaddon.config.NebulaeAddonConfig;
import beame.labyaddon.module.render.TargetESPModule;
import beame.labyaddon.ui.widget.CycleButton;
import beame.labyaddon.ui.widget.FloatSlider;
import beame.labyaddon.ui.widget.ToggleButton;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;

import java.util.Arrays;

/**
 * Simple in-game GUI that exposes a curated subset of the TargetESP settings.
 */
public class NebulaeAddonSettingsGui extends Screen {

    private final Screen parent;
    private final NebulaeAddonConfig config;
    private final TargetESPModule targetEsp;

    public NebulaeAddonSettingsGui(Screen parent, NebulaeAddonConfig config, TargetESPModule targetEsp) {
        super(new StringTextComponent("Nebulae Addon"));
        this.parent = parent;
        this.config = config;
        this.targetEsp = targetEsp;
    }

    @Override
    protected void init() {
        super.init();
        this.children.clear();
        this.buttons.clear();

        int centerX = this.width / 2;
        int columnWidth = 150;
        int leftX = centerX - columnWidth / 2;
        int startY = this.height / 6 + 10;
        int yOffset = 0;

        // TargetESP controls
        this.addButton(new ToggleButton(leftX, startY + yOffset, columnWidth, 20,
                "TargetESP", targetEsp::isEnabled, value -> {
                    targetEsp.setEnabled(value);
                    config.targetEspEnabled.set(value);
                }));
        yOffset += 24;
        this.addButton(new ToggleButton(leftX, startY + yOffset, columnWidth, 20,
                "Краснеть при ударе", targetEsp::isRedOnHurt, value -> {
                    targetEsp.setRedOnHurt(value);
                    config.redOnHurt.set(value);
                }));
        yOffset += 24;
        this.addButton(new CycleButton(leftX, startY + yOffset, columnWidth, 20,
                "Тип", targetEsp::getType, value -> {
                    targetEsp.setType(value);
                    config.targetEspType.set(value);
                }, () -> Arrays.asList(targetEsp.getAvailableTypes())));
        yOffset += 24;
        this.addButton(new FloatSlider(leftX, startY + yOffset, columnWidth, 20,
                "Скорость призраков", targetEsp::getGhostsSpeed, value -> {
                    targetEsp.setGhostsSpeed(value);
                    config.ghostsSpeed.set((double) value);
                },
                5.0F, 100.0F, 1.0F));
        yOffset += 24;
        this.addButton(new FloatSlider(leftX, startY + yOffset, columnWidth, 20,
                "Длина призраков", targetEsp::getGhostsLength, value -> {
                    targetEsp.setGhostsLength(value);
                    config.ghostsLength.set((double) value);
                },
                5.0F, 64.0F, 1.0F));
        yOffset += 24;
        this.addButton(new FloatSlider(leftX, startY + yOffset, columnWidth, 20,
                "Ширина призраков", targetEsp::getGhostsWidth, value -> {
                    targetEsp.setGhostsWidth(value);
                    config.ghostsWidth.set((double) value);
                },
                0.1F, 1.0F, 0.01F));
        yOffset += 24;
        this.addButton(new FloatSlider(leftX, startY + yOffset, columnWidth, 20,
                "Угол призраков", targetEsp::getGhostsAngle, value -> {
                    targetEsp.setGhostsAngle(value);
                    config.ghostsAngle.set((double) value);
                },
                0.01F, 1.0F, 0.01F));
        yOffset += 24;
        this.addButton(new FloatSlider(leftX, startY + yOffset, columnWidth, 20,
                "Скорость круга", targetEsp::getCircleSpeed, value -> {
                    targetEsp.setCircleSpeed(value);
                    config.speedCircle.set((double) value);
                },
                10.0F, 10000.0F, 1.0F));

        this.addButton(new Button(centerX - 75, this.height - 30, 150, 20,
                new StringTextComponent("Готово"), button -> Minecraft.getInstance().displayGuiScreen(parent)));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        drawCenteredString(matrixStack, this.font, this.getTitle(), this.width / 2, 15, 0xFFFFFF);
        drawCenteredString(matrixStack, this.font, "TargetESP", this.width / 2, this.height / 6, 0x5AA8FF);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().displayGuiScreen(parent);
    }
}
