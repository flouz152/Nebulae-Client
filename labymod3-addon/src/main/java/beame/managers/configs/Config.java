package beame.managers.configs;

import beame.Essence;
import beame.components.command.AbstractCommand;
import beame.feature.notify.NotificationManager;
import lombok.Getter;
import beame.module.Module;
import beame.module.ModuleList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import beame.setting.ConfigSetting;
import beame.setting.SettingList.*;

import java.io.File;
import java.util.*;

public class Config {
// leaked by itskekoff; discord.gg/sk3d jfne0xEE
    private File file;
    @Getter
    public String name;

    private static final Log log = LogFactory.getLog(Config.class);


    public Config(String name) {
        this.name = name;
    }

    public String save() {
        String configContent = "";
        try {
            JSONArray ja = new JSONArray();
            ja.put(Essence.getHandler().themeManager.currentTheme);

            JSONArray modules = new JSONArray();
            for(Module module : Essence.getHandler().getModuleList().getModules()){
                JSONArray inmodulesettings = new JSONArray();
                if(!module.getConfigSettings().isEmpty()){
                    for(ConfigSetting a : module.getConfigSettings()){
                        JSONArray value = new JSONArray();

                        String setting = "";
                        if (a instanceof BindSetting) {
                            setting = "BindSetting";
                        } else if (a instanceof BooleanSetting) {
                            setting = "BooleanSetting";
                        } else if (a instanceof LabelSetting) {
                            continue;
                        } else if (a instanceof RadioSetting) {
                            setting = "RadioSetting";
                        } else if (a instanceof EnumSetting) {
                            setting = "MultiRadioSetting";
                        } else if (a instanceof SliderSetting) {
                            setting = "SliderSetting";
                        } else if (a instanceof InputFieldSetting) {
                            setting = "InputFieldSetting";
                        }

                        value.put(a.getName());
                        value.put(setting);
                        if (a instanceof EnumSetting) {
                            EnumSetting p = (EnumSetting) a;
                            JSONArray mmod = new JSONArray();
                            for(BooleanSetting v : p.get()) {
                                JSONArray submmod = new JSONArray();
                                submmod.put(v.getName());
                                submmod.put(v.get());
                                mmod.put(submmod);
                            }
                            value.put(mmod);
                        } else {
                            value.put(a.get());
                        }

                        inmodulesettings.put(value);
                    }
                }

                JSONArray modulea = new JSONArray();
                modulea.put(module.getName());
                modulea.put(module.isState());
                modulea.put(module.getBind());
                modulea.put(inmodulesettings);

                if(module.isVisible()) {
                    modules.put(modulea);
                }
            }

            ja.put(modules);

            if (!name.equals("automaticsaved")) {
                Essence.getHandler().notificationManager.pushNotify("Конфиг успешно сохранен!", NotificationManager.Type.Saved);
            }
            configContent = ja.toString();
        } catch (Exception e) {
            System.err.println("Ошибка при сериализации данных конфигурации: " + e.getMessage());
            e.printStackTrace();
        }

        return configContent;
    }


    public CfgResult load(String array) {
        if (array == null || array.trim().isEmpty() || !array.startsWith("[")) {
            log.error("Некорректный формат конфигурации: строка пуста или не начинается с '['");
            AbstractCommand.addMessage("Ошибка: некорректный формат конфигурации");
            return new CfgResult(false, "Некорректный формат конфигурации");
        }

        JSONArray config;
        try {
            config = new JSONArray(array);
        } catch (JSONException e) {
            log.error("Ошибка разбора JSON конфигурации: " + e.getMessage(), e);
            AbstractCommand.addMessage("Ошибка разбора конфигурации: " + e.getMessage());
            return new CfgResult(false, "Ошибка разбора JSON");
        }

        try {
            if (config.length() < 2) {
                log.error("Конфигурация слишком короткая: ожидается массив с версией и модулями");
                return new CfgResult(false, "Неполная конфигурация");
            }

            JSONArray modules = config.getJSONArray(1);
            for (int i = 0; i < modules.length(); i++) {
                JSONArray module;
                try {
                    module = modules.getJSONArray(i);
                } catch (JSONException e) {
                    log.warn("Пропущен модуль на индексе " + i + ": " + e.getMessage());
                    continue;
                }

                String moduleName = module.getString(0);
                boolean moduleValue = module.getBoolean(1);
                int moduleBind = module.getInt(2);

                boolean moduleFound = false;
                for (ModuleList.MModule m : Essence.getHandler().getModuleList().replacments) {
                    if (m.name.equals(moduleName)) {
                        moduleFound = true;
                        m.module.markState(moduleValue);
                        if (moduleBind != 0) {
                            m.module.setBind(moduleBind);
                        }

                        JSONArray moduleSettings = module.getJSONArray(3);
                        if (moduleSettings.length() > 0) {
                            for (int j = 0; j < moduleSettings.length(); j++) {
                                try {
                                    JSONArray setting = moduleSettings.getJSONArray(j);
                                    String settingName = setting.getString(0);
                                    String settingType = setting.getString(1);
                                    Object settingValue = setting.get(2);

                                    if (!"MultiRadioSetting".equals(settingType)) {
                                        for (ConfigSetting set : m.module.getConfigSettings()) {
                                            if (set.getName().equals(settingName)) {
                                                try {
                                                    if ("SliderSetting".equals(settingType)) {
                                                        if (set.get() instanceof Float) {
                                                            set.set(Float.parseFloat(settingValue.toString()));
                                                        } else if (set.get() instanceof Integer) {
                                                            set.set(Integer.parseInt(settingValue.toString()));
                                                        } else if (set.get() instanceof Double) {
                                                            set.set(Double.parseDouble(settingValue.toString()));
                                                        }
                                                    } else {
                                                        set.set(settingValue);
                                                    }
                                                } catch (Exception e) {
                                                    log.warn("Ошибка установки настройки '" + settingName + "' для модуля '" + moduleName + "': " + e.getMessage());
                                                }
                                            }
                                        }
                                    } else {
                                        JSONArray multiSettings = (JSONArray) settingValue;
                                        for (int k = 0; k < multiSettings.length(); k++) {
                                            JSONArray multiSetting = multiSettings.getJSONArray(k);
                                            String key = multiSetting.getString(0);
                                            boolean value = multiSetting.getBoolean(1);

                                            for (ConfigSetting set : m.module.getConfigSettings()) {
                                                if (set.getName().equals(settingName)) {
                                                    List<BooleanSetting> multi = (List<BooleanSetting>) set.get();
                                                    for (BooleanSetting bool : multi) {
                                                        if (bool.getName().equals(key)) {
                                                            bool.set(value);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    log.warn("Ошибка обработки настройки для модуля '" + moduleName + "' на индексе " + j + ": " + e.getMessage());
                                }
                            }
                        }
                        break;
                    }
                }
                if (!moduleFound) {
                    log.warn("Модуль '" + moduleName + "' не найден в списке модулей");
                }
            }
        } catch (Exception e) {
            log.error("Ошибка загрузки конфигурации: " + e.getMessage(), e);
            AbstractCommand.addMessage("Ошибка загрузки конфигурации: " + e.getMessage());
            return new CfgResult(false, "Ошибка загрузки конфигурации");
        }

        if (Essence.getHandler().notificationManager != null) Essence.getHandler().notificationManager.pushNotify("Конфигурация '" + name + "' успешно загружена!", NotificationManager.Type.Loaded);
        return new CfgResult(true);
    }

    public class CfgResult {
        public boolean isSuccesful;
        public String error = "";

        public CfgResult(boolean isSuccesful){
            this.isSuccesful = isSuccesful;
            this.error = "None";
        }

        public CfgResult(boolean isSuccesful, String error){
            this.isSuccesful = isSuccesful;
            this.error = error;
        }
    }
}
