package beame.setting.Render;

import beame.util.color.ColorUtils;
import beame.util.fonts.Fonts;
import beame.util.math.MathUtil;
import beame.util.render.ClientHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import lombok.Setter;

public class SearchRender {
// leaked by itskekoff; discord.gg/sk3d 3MKvw9FF
    @Setter
    private int x;
    @Setter
    private int y;
    @Setter
    private int width;
    @Setter
    private int height;
    @Setter
    private String text;
    private boolean isFocused;
    private boolean typing;
    private final String placeholder;

    public SearchRender(int x, int y, int width, int height, String string) {
        this.x = x;
        this.y = y + 45;
        this.width = width;
        this.height = height;
        this.placeholder = string;
        this.text = "";
        this.isFocused = false;
        this.typing = false;
    }

    public void render(MatrixStack matrixStack, int n, int n2, float f) {
        ClientHandler.drawRound(this.x, this.y, this.width, this.height, 5, -1);
        String string = this.text.isEmpty() && !this.typing ? this.placeholder : this.text;
        String string2 = this.typing && System.currentTimeMillis() % 1000L > 500L ? "_" : "";
        Fonts.SUISSEINTL.get(14).drawString(matrixStack, string + string2, (float)(this.x + 5), (float)(this.y + (this.height - 8) / 2 + 1), ColorUtils.rgb(0, 0, 0), 6);
    }

    public boolean charTyped(char c, int n) {
        if (this.isFocused) {
            this.text = this.text + c;
            return false;
        }
        return true;
    }

    public boolean keyPressed(int n, int n2, int n3) {
        if (this.isFocused && n == 259 && !this.text.isEmpty()) {
            this.text = this.text.substring(0, this.text.length() - 1);
            return false;
        }
        if (n == 257 || n == 256) {
            this.typing = false;
        }
        return true;
    }

    public boolean mouseClicked(double d, double d2, int n) {
        if (!MathUtil.isHovered((float)d, (float)d2, this.x, this.y, this.width, this.height)) {
            this.isFocused = false;
        }
        this.typing = this.isFocused = MathUtil.isHovered((float)d, (float)d2, this.x, this.y, this.width, this.height);
        return this.isFocused;
    }

    public boolean isEmpty() {
        return this.text.isEmpty();
    }

    public void setFocused(boolean bl) {
        this.isFocused = bl;
    }

    public void setTyping(boolean bl) {
        this.typing = bl;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public String getText() {
        return this.text;
    }

    public boolean isFocused() {
        return this.isFocused;
    }

    public boolean isTyping() {
        return this.typing;
    }

    public String getPlaceholder() {
        return this.placeholder;
    }
}
