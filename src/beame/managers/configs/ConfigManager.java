package beame.managers.configs;

import beame.Nebulae;
import beame.feature.notify.NotificationManager;
import net.minecraft.client.Minecraft;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConfigManager {
// leaked by itskekoff; discord.gg/sk3d DJceiX9W
    private static final Log log = LogFactory.getLog(ConfigManager.class);
    public List<Config> cfgs = new ArrayList<>();
    private static final String DEFAULT_CONFIG = "automaticsaved";
    private final File configDir;

    public ConfigManager() {
        this.configDir = new File(String.valueOf(Nebulae.getHandler().getFilesDir()));
        if (!configDir.exists()) {
            configDir.mkdirs();
        }
    }

    public void init() {
        refreshList();
        loadAutoConfig();
    }

    public void saveAutoConfig() {
        saveConfig(DEFAULT_CONFIG);
        //Nebulae.getHandler().notificationManager.pushNotify("Автоматическое сохранение конфигурации выполнено.", NotificationManager.Type.Info);
    }

    public void loadAutoConfig() {
        File configFile = new File(configDir, DEFAULT_CONFIG + ".json");
        if (configFile.exists()) {
            loadConfig(DEFAULT_CONFIG);
        } else {
            log.info("Автоматическая конфигурация не найдена, используется стандартная.");
        }
    }

    public void refreshList() {
        cfgs.clear();
        try {
            File[] matchingFiles = configDir.listFiles((dir, name) -> name.endsWith(".json"));
            if (matchingFiles != null) {
                for (File file : matchingFiles) {
                    cfgs.add(new Config(file.getName().replace(".json", "")));
                }
            }
        } catch (Exception e) {
            log.error("Ошибка при обновлении списка конфигураций", e);
        }
    }

    public void saveConfig(String configName) {
        File configFile = new File(configDir, configName + ".json");
        Config cfg = new Config(configName);
        String content = cfg.save();

        try (FileWriter writer = new FileWriter(configFile)) {
            writer.write(content);
            if (!configName.equals(DEFAULT_CONFIG)) {
                Nebulae.getHandler().notificationManager.pushNotify("Конфигурация '" + configName + "' сохранена.", NotificationManager.Type.Info);
            }
        } catch (IOException e) {
            log.error("Ошибка при сохранении конфигурации: " + configName, e);
        }
    }

    public void loadConfig(String configName) {
        File configFile = new File(configDir, configName + ".json");
        if (!configFile.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
            String content = reader.readLine();
            new Config(configName).load(content);
            if (Nebulae.getHandler().notificationManager != null)
                Nebulae.getHandler().notificationManager.pushNotify("Конфигурация '" + configName + "' загружена.", NotificationManager.Type.Info);
        } catch (IOException e) {
            log.error("Ошибка при загрузке конфигурации: " + configName, e);
        }
    }

    public void deleteConfig(String configName) {
        File configFile = new File(configDir, configName + ".json");
        if (configFile.exists() && configFile.delete()) {
            Nebulae.getHandler().notificationManager.pushNotify("Конфигурация '" + configName + "' удалена.", NotificationManager.Type.Info);
        } else {
            log.error("Ошибка при удалении конфигурации: " + configName);
        }
        refreshList();
    }

    public void clearConfig() {
        try {
            File[] files = configDir.listFiles();
            if (files == null || files.length == 0) {
                Nebulae.getHandler().notificationManager.pushNotify("Папка конфигураций пуста.", NotificationManager.Type.Info);
                return;
            }
            for (File file : files) {
                file.delete();
            }
            Nebulae.getHandler().notificationManager.pushNotify("Все конфигурации очищены.", NotificationManager.Type.Info);
            refreshList();
        } catch (Exception e) {
            log.error("Ошибка при очистке конфигураций", e);
        }
    }
}