package beame.util.drag;

import beame.util.math.ScaleMath;
import beame.util.math.Vec2i;
import beame.util.render.ClientHandler;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import net.minecraft.client.MainWindow;

public class Dragging {
// leaked by itskekoff; discord.gg/sk3d OOha6Lan
    @Expose
    @SerializedName(value="x")
    private float xPos;
    @Expose
    @SerializedName(value="y")
    private float yPos;
    public float initialXVal;
    public float initialYVal;
    private float startX;
    private float startY;
    @Getter
    private boolean dragging;
    private float width;
    private float height;
    @Expose
    @SerializedName(value="name")
    private String name;

    public Dragging(String name, float initialXVal, float initialYVal) {
        this.name = name;
        this.xPos = initialXVal;
        this.yPos = initialYVal;
        this.initialXVal = initialXVal;
        this.initialYVal = initialYVal;
    }

    public String getName() {
        return this.name;
    }

    public float getWidth() {
        return this.width;
    }

    public void setWidth(float width2) {
        this.width = width2;
    }

    public float getHeight() {
        return this.height;
    }

    public void setHeight(float height2) {
        this.height = height2;
    }

    public float getX() {
        return this.xPos;
    }

    public void setX(float x) {
        this.xPos = x;
    }

    public float getY() {
        return this.yPos;
    }

    public void setY(float y) {
        this.yPos = y;
    }

    public final void onDraw(int mouseX, int mouseY, MainWindow res) {
        Vec2i fixed = ScaleMath.getMouse(mouseX, mouseY);
        mouseX = fixed.getX();
        if (this.dragging) {
            float oldX = this.xPos;
            float oldY = this.yPos;
            
            this.xPos = (float)mouseX - this.startX;
            this.yPos = (float)mouseY - this.startY;
            if (this.xPos + this.width > (float)res.scaledWidth()) {
                this.xPos = (float)res.scaledWidth() - this.width;
            }
            if (this.yPos + this.height > (float)res.scaledHeight()) {
                this.yPos = (float)res.scaledHeight() - this.height;
            }
            if (this.xPos < 0.0f) {
                this.xPos = 0.0f;
            }
            if (this.yPos < 0.0f) {
                this.yPos = 0.0f;
            }
            if (oldX != this.xPos || oldY != this.yPos) {
                beame.util.drag.DraggableManager.save();
            }
        }
    }

    public final void onClick(double mouseX, double mouseY, int button) {
        Vec2i fixed = ScaleMath.getMouse((int)mouseX, (int)mouseY);
        mouseX = fixed.getX();
        mouseY = fixed.getY();
        if (button == 0 && ClientHandler.isInRegion((int)mouseX, (int)mouseY,(int) this.xPos, (int)this.yPos, (int)this.width, (int)this.height)) {
            this.dragging = true;
            this.startX = (int)(mouseX - (double)this.xPos);
            this.startY = (int)(mouseY - (double)this.yPos);
        }
    }

    public final void onRelease(int button) {
        if (button == 0) {
            this.dragging = false;
            // Автоматически сохраняем позиции при отпускании мыши
            beame.util.drag.DraggableManager.save();
        }
    }
}


