package com.nebulae.clickgui.client.data;

import java.util.List;

public class CategoryData {
    private final String name;
    private final String icon;
    private final List<ModuleData> modules;

    public CategoryData(String name, String icon, List<ModuleData> modules) {
        this.name = name;
        this.icon = icon;
        this.modules = modules;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public List<ModuleData> getModules() {
        return modules;
    }
}
