package beame.components.command.impl.game;

import beame.Nebulae;
import beame.components.command.AbstractCommand;

import java.util.List;

@AbstractCommand.CommandInfo(name = "gps", description = "Метка к точке")
public class GPSCommand extends AbstractCommand {
// leaked by itskekoff; discord.gg/sk3d MxoeFHcx
    @Override
    public void run(String[] args) {
        if (args.length < 2) {
            error();
            return;
        }

        if (args[1].equalsIgnoreCase("off")) {
            Nebulae.getHandler().gps.disable();
            addMessage("GPS отключен.");
            return;
        }

        try {
            int x, z;

            if (args.length == 3) {
                x = Integer.parseInt(args[1]);
                z = Integer.parseInt(args[2]);
            } else if (args.length == 4) {
                x = Integer.parseInt(args[1]);
                z = Integer.parseInt(args[3]);
            } else {
                error();
                return;
            }

            Nebulae.getHandler().gps.init(x, z);
            addMessage("GPS установлен на координаты: " + x + " " + z);
        } catch (NumberFormatException e) {
            error();
        }
    }

    @Override
    public void error() {
        printMessage(List.of(
                "[Ошибка] Используйте:",
                ".gps <x> <z>",
                ".gps <x> <y> <z>",
                ".gps off"
        ));
    }

    @Override
    public String name() {
        return "gps";
    }

    @Override
    public String description() {
        return "Метка к точке";
    }

    @Override
    public List<String> adviceMessage() {
        return List.of(
                ".gps <x> <z>",
                ".gps <x> <y> <z>",
                ".gps off"
        );
    }

    @Override
    public List<String> aliases() {
        return List.of();
    }
}
