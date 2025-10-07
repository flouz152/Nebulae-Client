# Nebulae LabyMod 3 Add-on

This directory contains a LabyMod 3 add-on that bridges the existing Essence modules
`FTHelper` and `TargetESP` into a standalone, configurable package. The add-on is
designed to be built separately from the core client and exposes a light-weight
configuration screen which allows toggling both modules and adjusting their most
important options without opening the full Essence click GUI.

## Structure

```
laby-addon/
├── README.md
└── src
    └── main
        └── java
            └── beame
                └── labyaddon
                    ├── NebulaeAddon.java
                    ├── config
                    │   └── NebulaeAddonConfig.java
                    ├── feature
                    │   ├── FTHelperBridge.java
                    │   └── TargetESPBridge.java
                    └── ui
                        ├── NebulaeAddonSettingsGui.java
                        └── widget
                            ├── CycleButton.java
                            ├── FloatSlider.java
                            └── ToggleButton.java
```

## Building

The add-on can be packaged with your preferred build tool. The sources do not rely on
the Gradle configuration of the main client, so they can be dropped into a dedicated
LabyMod 3 project if desired. All references to Essence internals are wrapped in
bridge classes, making it easy to adjust or replace the underlying modules.

