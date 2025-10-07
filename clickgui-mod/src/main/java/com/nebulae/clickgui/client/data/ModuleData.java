package com.nebulae.clickgui.client.data;

public class ModuleData {
    private final String name;
    private final String description;

    public ModuleData(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
