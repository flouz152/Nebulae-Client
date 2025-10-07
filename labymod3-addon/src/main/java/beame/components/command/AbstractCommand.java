package beame.components.command;

import beame.util.IMinecraft;
import beame.util.chat.ColorFormatter;
import beame.util.color.ColorUtils;
import lombok.Getter;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

@Getter
public abstract class AbstractCommand implements IMinecraft {
// leaked by itskekoff; discord.gg/sk3d 6UFJgAuH
    public final String command = this.getClass().getAnnotation(CommandInfo.class).name();
    public final String description = this.getClass().getAnnotation(CommandInfo.class).description();
    public static final String tag = TextFormatting.BLUE + "essence" + TextFormatting.GRAY + " > " + TextFormatting.RESET;

    public abstract String name();

    public abstract String description();

    public abstract List<String> adviceMessage();

    public abstract List<String> aliases();

    public abstract void run(String[] args) throws Exception;

    public abstract void error();

//
//    private Stream<String> tabComplete() {
//        try {
//            return this.command.tabComplete(this.label, this.args);
//        } catch(CommandException ignored) {
//            // NOP
//        } catch(Throwable t) {
//            t.printStackTrace();
//        }
//        return Stream.empty();
//    }

    public void out(String message) {
        addMessage(message);
        System.out.println("[cmd -> out] " + message);
    }

    public void printMessage(List<String> messages) {
        messages.forEach(this::out);
    }

    public static void addMessage(Object message) {
        try {
            mc.ingameGUI.getChatGUI().printChatMessage(new StringTextComponent(tag + ColorFormatter.get((String) message)));
        } catch (Exception ignored) {
        }
    }

    public static IFormattableTextComponent genGradientText(String text, Color color1, Color color2) {
        IFormattableTextComponent gradientComponent = new StringTextComponent("");
        Color[] color = ColorUtils.genGradientForText(color1, color2, text.length());
        int i = 0;
        for (char ch : text.toCharArray()) {
            IFormattableTextComponent component = new StringTextComponent(String.valueOf(ch));
            Style style = new Style(net.minecraft.util.text.Color.fromInt(color[i].getRGB()), false, false, false, false, false, null, null, null, null);
            component.setStyle(style);
            gradientComponent.append(component);
            i++;
        }
        return gradientComponent;
    }

    @Retention(value= RetentionPolicy.RUNTIME)
    public @interface CommandInfo {
        String name();
        String description();
    }
}
