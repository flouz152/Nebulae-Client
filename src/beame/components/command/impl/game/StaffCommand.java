package beame.components.command.impl.game;

import beame.Nebulae;
import beame.components.command.AbstractCommand;
import beame.managers.staff.StaffManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@AbstractCommand.CommandInfo(name = "staff", description = "Управление списком персонала")
public class StaffCommand extends AbstractCommand {
// leaked by itskekoff; discord.gg/sk3d 4sBi4gPK

    @Override
    public void run(String[] args) {
        if (args.length < 2) {
            error();
            return;
        }

        String action = args[1].toLowerCase();

        Map<String, Runnable> actions = Map.of(
                "list", this::getStaffList,
                "clear", this::clearStaffList
        );

        Map<String, Consumer<String>> paramActions = Map.of(
                "add", this::addStaffToList,
                "delete", this::deleteStaffFromList
        );

        if (paramActions.containsKey(action)) {
            if (args.length < 3) {
                error();
                return;
            }
            paramActions.get(action).accept(args[2]);
        } else if (actions.containsKey(action)) {
            actions.get(action).run();
        } else {
            error();
        }
    }

    private void addStaffToList(String staffName) {
        String realPlayerName = Minecraft.getInstance().player.getGameProfile().getName();
        String displayedName = Minecraft.getInstance().player.getName().getString();

        if (staffName.equalsIgnoreCase(realPlayerName) || staffName.equalsIgnoreCase(displayedName)) {
            addMessage(TextFormatting.RED + "Нельзя добавить себя в список персонала.");
            return;
        }

        if (staffManager().isStaff(staffName)) {
            addMessage(TextFormatting.RED + "Игрок " + staffName + " уже в списке персонала.");
            return;
        }

        staffManager().add(staffName);
        addMessage("Игрок " + TextFormatting.BLUE + staffName + TextFormatting.WHITE + " добавлен в список персонала.");
    }

    private void deleteStaffFromList(String staffName) {
        if (!staffManager().isStaff(staffName)) {
            addMessage(TextFormatting.RED + "Игрок " + staffName + " не найден в списке персонала.");
            return;
        }

        staffManager().remove(staffName);
        addMessage("Игрок " + TextFormatting.BLUE + staffName + TextFormatting.WHITE + " удален из списка персонала.");
    }

    private void getStaffList() {
        if (staffManager().getStaffs().isEmpty()) {
            addMessage(TextFormatting.RED + "Список персонала пуст.");
            return;
        }

        String staffList = String.join(", ", staffManager().getStaffs());
        addMessage("Список персонала: " + TextFormatting.BLUE + staffList);
    }

    private void clearStaffList() {
        if (staffManager().getStaffs().isEmpty()) {
            addMessage(TextFormatting.RED + "Список персонала уже пуст.");
            return;
        }

        staffManager().clear();
        addMessage("Список персонала очищен.");
    }

    private static StaffManager staffManager() {
        return Nebulae.getHandler().getStaffManager();
    }

    @Override
    public void error() {
        printMessage(adviceMessage());
    }

    @Override
    public String name() {
        return "staff";
    }

    @Override
    public String description() {
        return "Управление списком персонала клиента";
    }

    @Override
    public List<String> adviceMessage() {
        return List.of(
                TextFormatting.RED + "[Ошибка] Используйте команды:",
                ".staff add <ник> - Добавить иг religionsрока в список персонала",
                ".staff delete <ник> - Удалить игрока из списка персонала",
                ".staff list - Показать список персонала",
                ".staff clear - Очистить список персонала",
                "Пример: " + TextFormatting.BLUE + ".staff add chipsina"
        );
    }

    @Override
    public List<String> aliases() {
        return List.of("st", "admin");
    }
}