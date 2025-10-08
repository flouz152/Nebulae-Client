package beame.util.render;

import beame.components.command.AbstractCommand;
import beame.util.IMinecraft;
import com.mojang.blaze3d.systems.IRenderCall;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.StringTextComponent;

import java.util.LinkedList;
import java.util.Queue;

public interface IWrapper extends IMinecraft {
// leaked by itskekoff; discord.gg/sk3d BTIvC283

    Queue<IRenderCall> blurQueue = new LinkedList<>();

    static void clearQueue() {
        if (!blurQueue.isEmpty()) {
            blurQueue.clear();
        }
    }

    static void executeQueue(boolean blur) {
        //AbstractCommand.addMessage("Blur: " + blur + ", Queue size: " + blurQueue.size());

        Queue<IRenderCall> currentQueue = new LinkedList<>(blurQueue);
        blurQueue.clear();

        if (!blur) {
            return;
        }
        

        if (!currentQueue.isEmpty()) {
            KawaseBlur.applyBlur(() -> {
                while (!currentQueue.isEmpty()) {
                    currentQueue.poll().execute();
                }
            }, 1, 1.6f);
        }
    }
}
