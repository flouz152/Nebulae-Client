package beame.components.command.impl.software.keybinds;

import beame.Nebulae;
import beame.components.command.AbstractCommand;
import beame.util.BindMapping;
import beame.module.Module;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

@AbstractCommand.CommandInfo(name = "bind", description = "Бинды")
public class BindsCommand extends AbstractCommand {
// leaked by itskekoff; discord.gg/sk3d xDtMdHdz

    @Override
    public void run(String[] args) {
        if (args.length < 2 || !args[1].equalsIgnoreCase("list")) {
            error();
            return;
        }

        StringBuilder bindList = new StringBuilder("Список биндов:\n");
        List<Module> modules = Nebulae.getHandler().getModuleList().getModules();

        for (int i = 0; i < modules.size(); i++) {
            Module module = modules.get(i);
            int bind = module.getBind();
            if (bind != -1 && bind != 0) {
                bindList.append(tag)
                        .append(TextFormatting.GRAY)
                        .append(module.getName())
                        .append(": ")
                        .append(TextFormatting.BLUE)
                        .append(BindMapping.getReverseKey(bind));
                if (i < modules.size() - 1) bindList.append("\n");
                else bindList.append(".");
            }
        }

        addMessage(bindList.toString());
    }

    @Override
    public void error() {
        printMessage(List.of(
                "[Ошибка] Используйте:",
                ".bind list"
        ));
    }

    @Override
    public String name() {
        return "bind";
    }

    @Override
    public String description() {
        return "Бинды";
    }

    @Override
    public List<String> adviceMessage() {
        return List.of("bind list - Показать список всех биндов");
    }

    @Override
    public List<String> aliases() {
        return List.of();
    }
}
