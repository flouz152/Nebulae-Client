package beame.components.command.impl.software.keybinds;

import beame.Nebulae;
import beame.components.command.AbstractCommand;
import beame.util.other.KeyStorage;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@AbstractCommand.CommandInfo(name = "macro", description = "Управление макросами")
public class MacroCommand extends AbstractCommand {
// leaked by itskekoff; discord.gg/sk3d LVXauM2Y

    @Override
    public void run(String[] args) {
        if (args.length < 2) {
            error();
            return;
        }

        String action = args[1].toLowerCase();
        var macroManager = Nebulae.getHandler().getMacroManager();

        Map<String, Runnable> actions = Map.of(
                "list", this::printMacrosList,
                "clear", macroManager::clearList
        );

        Map<String, Consumer<String[]>> paramActions = Map.of(
                "add", params -> addMacro(params[0], String.join(" ", java.util.Arrays.copyOfRange(params, 1, params.length))),
                "remove", params -> removeMacro(params[0])
        );

        if (paramActions.containsKey(action)) {
            if (args.length < 3) {
                error();
                return;
            }
            paramActions.get(action).accept(java.util.Arrays.copyOfRange(args, 2, args.length));
        } else if (actions.containsKey(action)) {
            actions.get(action).run();
        } else {
            error();
        }
    }

    private void addMacro(String macroKey, String macroMessage) {
        Integer key = KeyStorage.getKey(macroKey.toUpperCase());
        if (key == null) {
            addMessage("Клавиша " + macroKey + " не найдена!");
            return;
        }

        if (Nebulae.getHandler().getMacroManager().hasMacro(macroKey)) {
            addMessage("Макрос с клавишей " + macroKey + " уже существует!");
            return;
        }

        Nebulae.getHandler().getMacroManager().addMacro(macroKey, macroMessage, key);
        addMessage("Добавлен макрос: " + TextFormatting.BLUE + macroKey + TextFormatting.WHITE + " -> " + TextFormatting.BLUE + macroMessage);
    }

    private void removeMacro(String macroKey) {
        if (!Nebulae.getHandler().getMacroManager().hasMacro(macroKey)) {
            addMessage("Макрос с клавишей " + macroKey + " не найден!");
            return;
        }

        Nebulae.getHandler().getMacroManager().deleteMacro(macroKey);
        addMessage("Макрос с клавишей " + TextFormatting.BLUE + macroKey + TextFormatting.WHITE + " успешно удален!");
    }

    private void printMacrosList() {
        var macroManager = Nebulae.getHandler().getMacroManager();
        if (macroManager.isEmpty()) {
            addMessage(TextFormatting.RED + "Список макросов пуст.");
            return;
        }

        String macros = macroManager.macroList.stream()
                .map(macro -> {
                    String keyName = GLFW.glfwGetKeyName(macro.getKey(), 0);
                    if (keyName == null) {
                        keyName = "UNKNOWN";
                    }
                    return macro.getName() + ": " + macro.getMessage();
                })
                .collect(Collectors.joining(", "));
        addMessage("Список макросов: " + TextFormatting.BLUE + macros);
    }

    @Override
    public void error() {
        printMessage(adviceMessage());
    }

    @Override
    public String name() {
        return "macro";
    }

    @Override
    public String description() {
        return "Управление макросами клиента";
    }

    @Override
    public List<String> adviceMessage() {
        return List.of(
                TextFormatting.RED + "[Ошибка] Используйте команды:",
                ".macro add <клавиша> <сообщение> - Добавить новый макрос",
                ".macro remove <клавиша> - Удалить макрос",
                ".macro list - Показать список макросов",
                ".macro clear - Очистить все макросы",
                "Пример: " + TextFormatting.BLUE + ".macro add A /home home"
        );
    }

    @Override
    public List<String> aliases() {
        return List.of("macros", "m");
    }
}