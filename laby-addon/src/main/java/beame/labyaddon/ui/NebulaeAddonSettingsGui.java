package beame.labyaddon.ui;

import beame.labyaddon.feature.FTHelperBridge;
import beame.labyaddon.feature.TargetESPBridge;
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
 * Simple in-game GUI that exposes a curated subset of the FTHelper and TargetESP settings.
 */
public class NebulaeAddonSettingsGui extends Screen {

    private final Screen parent;
    private final FTHelperBridge ftHelper;
    private final TargetESPBridge targetEsp;

    public NebulaeAddonSettingsGui(Screen parent, FTHelperBridge ftHelper, TargetESPBridge targetEsp) {
        super(new StringTextComponent("Nebulae Addon"));
        this.parent = parent;
        this.ftHelper = ftHelper;
        this.targetEsp = targetEsp;
    }

    @Override
    protected void init() {
        super.init();
        this.children.clear();
        this.buttons.clear();

        int centerX = this.width / 2;
        int columnWidth = 150;
        int leftX = centerX - columnWidth - 10;
        int rightX = centerX + 10;
        int startY = this.height / 6 + 10;
        int yOffset = 0;

        // FTHelper controls
        this.addButton(new ToggleButton(leftX, startY + yOffset, columnWidth, 20,
                "FTHelper", ftHelper::isEnabled, ftHelper::setEnabled));
        yOffset += 24;
        this.addButton(new ToggleButton(leftX, startY + yOffset, columnWidth, 20,
                "Авто GPS", () -> ftHelper.getOption("Авто GPS", true),
                value -> ftHelper.setOption("Авто GPS", value)));
        yOffset += 24;
        this.addButton(new ToggleButton(leftX, startY + yOffset, columnWidth, 20,
                "Конвертация времени", () -> ftHelper.getOption("Конвертировать время", true),
                value -> ftHelper.setOption("Конвертировать время", value)));
        yOffset += 24;
        this.addButton(new ToggleButton(leftX, startY + yOffset, columnWidth, 20,
                "Раскрывать баны", () -> ftHelper.getOption("Раскрывать баны", true),
                value -> ftHelper.setOption("Раскрывать баны", value)));
        yOffset += 24;
        this.addButton(new ToggleButton(leftX, startY + yOffset, columnWidth, 20,
                "Авто /event delay", () -> ftHelper.getOption("Авто /event delay", true),
                value -> ftHelper.setOption("Авто /event delay", value)));
        yOffset += 24;
        this.addButton(new ToggleButton(leftX, startY + yOffset, columnWidth, 20,
                "Улучшать команды", () -> ftHelper.getOption("Улучшать команды", false),
                value -> ftHelper.setOption("Улучшать команды", value)));
        yOffset += 24;
        this.addButton(new FloatSlider(leftX, startY + yOffset, columnWidth, 20,
                "Интервал event", ftHelper::getEventDelayMinutes, ftHelper::setEventDelayMinutes,
                1.0F, 10.0F, 1.0F));

        // TargetESP controls
        int espYOffset = 0;
        this.addButton(new ToggleButton(rightX, startY + espYOffset, columnWidth, 20,
                "TargetESP", targetEsp::isEnabled, targetEsp::setEnabled));
        espYOffset += 24;
        this.addButton(new ToggleButton(rightX, startY + espYOffset, columnWidth, 20,
                "Краснеть при ударе", targetEsp::isRedOnHurt, targetEsp::setRedOnHurt));
        espYOffset += 24;
        this.addButton(new CycleButton(rightX, startY + espYOffset, columnWidth, 20,
                "Тип", targetEsp::getType, targetEsp::setType, () -> Arrays.asList(targetEsp.getAvailableTypes())));
        espYOffset += 24;
        this.addButton(new FloatSlider(rightX, startY + espYOffset, columnWidth, 20,
                "Скорость призраков", targetEsp::getGhostsSpeed, targetEsp::setGhostsSpeed,
                5.0F, 100.0F, 1.0F));
        espYOffset += 24;
        this.addButton(new FloatSlider(rightX, startY + espYOffset, columnWidth, 20,
                "Длина призраков", targetEsp::getGhostsLength, targetEsp::setGhostsLength,
                5.0F, 64.0F, 1.0F));
        espYOffset += 24;
        this.addButton(new FloatSlider(rightX, startY + espYOffset, columnWidth, 20,
                "Ширина призраков", targetEsp::getGhostsWidth, targetEsp::setGhostsWidth,
                0.1F, 1.0F, 0.01F));
        espYOffset += 24;
        this.addButton(new FloatSlider(rightX, startY + espYOffset, columnWidth, 20,
                "Угол призраков", targetEsp::getGhostsAngle, targetEsp::setGhostsAngle,
                0.01F, 1.0F, 0.01F));
        espYOffset += 24;
        this.addButton(new FloatSlider(rightX, startY + espYOffset, columnWidth, 20,
                "Скорость круга", targetEsp::getCircleSpeed, targetEsp::setCircleSpeed,
                10.0F, 10000.0F, 1.0F));

        this.addButton(new Button(centerX - 75, this.height - 30, 150, 20,
                new StringTextComponent("Готово"), button -> Minecraft.getInstance().displayGuiScreen(parent)));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        drawCenteredString(matrixStack, this.font, this.getTitle(), this.width / 2, 15, 0xFFFFFF);
        drawString(matrixStack, this.font, "FTHelper", this.width / 2 - 160, this.height / 6, 0xFFD25A);
        drawString(matrixStack, this.font, "TargetESP", this.width / 2 + 70, this.height / 6, 0x5AA8FF);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().displayGuiScreen(parent);
    }
}
