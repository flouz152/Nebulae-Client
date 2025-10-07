package beame.screens.api;

import beame.Nebulae;
import beame.util.color.ColorUtils;
import beame.util.fonts.Fonts;
import beame.util.render.ClientHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;

public class NebulaeButton extends AbstractButton {
// leaked by itskekoff; discord.gg/sk3d CB4IaJtH
    public String text;
    public int x, y, width, height;
    public Button.IPressable callback;

    public boolean hover = false;

    public NebulaeButton(String text, int x, int y, int width, int height, Button.IPressable callback) {
        super(x, y, width, height, ITextComponent.getTextComponentOrEmpty("nebulaebutton_" + text));
        this.text = text;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.callback = callback;
    }

    public void render(MatrixStack matrixStack) {
        ClientHandler.drawRound(x, y, width, height, 3, ColorUtils.getColor(20, 20, 20, hover ? 80 : 50));
        Fonts.SF_BOLD.get(14).drawCenteredString(matrixStack, text, x + width / 2 - 1, y + height / 2 - 1.5f, hover ? Nebulae.getHandler().themeManager.getColor(180) : -1);
    }

    @Override
    public void onPress() {
        callback.onPress(new Button(x, y, width, height, ITextComponent.getTextComponentOrEmpty("nebulaebutton2_" + text), callback));
    }
}
