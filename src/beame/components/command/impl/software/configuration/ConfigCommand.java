package beame.components.command.impl.software.configuration;

import beame.Nebulae;
import beame.components.command.AbstractCommand;
import beame.managers.configs.ConfigManager;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@AbstractCommand.CommandInfo(name = "cfg", description = "Управление конфигурациями")
public class ConfigCommand extends AbstractCommand {
// leaked by itskekoff; discord.gg/sk3d 8gNoKrzz

    @Override
    public void run(String[] args) {
        if (args.length < 2) {
            error();
            return;
        }

        String action = args[1].toLowerCase();
        ConfigManager cfgManager = Nebulae.cfgManager;

        Map<String, Runnable> actions = Map.of(
                "list", () -> listConfigs(cfgManager),
                "dir", this::openDirectory,
                "clear", cfgManager::clearConfig,
                "reset", this::resetModules
        );

        Map<String, Consumer<String>> paramActions = Map.of(
                "load", cfgManager::loadConfig,
                "save", cfgManager::saveConfig,
                "remove", cfgManager::deleteConfig
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

    private void listConfigs(ConfigManager cfgManager) {
        cfgManager.refreshList();
        if (cfgManager.cfgs.isEmpty()) {
            addMessage("Конфигов нет!");
            return;
        }
        String configs = cfgManager.cfgs.stream()
                .map(cfg -> cfg.name.replace(".json", ""))
                .collect(Collectors.joining(", "));
        addMessage("Список конфигураций: " + configs);
    }

    private void openDirectory() {
        try {
            Runtime.getRuntime().exec("explorer " + Nebulae.getHandler().getFilesDir().toFile().getAbsolutePath());
        } catch (Exception e) {
            addMessage("Не удалось открыть директорию.");
        }
    }

    private void resetModules() {
        Nebulae.getHandler().getModuleList().getModules().forEach(module -> {
            if (module.state) module.setState(false);
        });
        addMessage("Конфигурация успешно сброшена!");
    }

    @Override
    public void error() {
        printMessage(adviceMessage());
    }

    @Override
    public String name() {
        return "cfg";
    }

    @Override
    public String description() {
        return "Управление конфигурациями клиента";
    }

    @Override
    public List<String> adviceMessage() {
        return List.of(
                "[Ошибка] Используйте команды:",
                ".cfg load <name>",
                ".cfg save <name>",
                ".cfg remove <name>",
                ".cfg list",
                ".cfg clear",
                ".cfg reset",
                ".cfg dir"
        );
    }

    @Override
    public List<String> aliases() {
        return List.of("config", "configuration");
    }
}
