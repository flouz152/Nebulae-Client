# Nebulae LabyMod 3 Add-on

This directory contains a LabyMod 3 add-on that ships standalone versions of the
`FTHelper` and `TargetESP` modules.  The implementation is completely
self-contained – all required helpers and render logic live inside the add-on so
it can be packaged independently from the main Essence client.  Players can
toggle both features and tweak their most important options through a compact
in-game configuration screen.

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
                    ├── core
                    │   ├── AddonModule.java
                    │   └── ModuleManager.java
                    ├── module
                    │   ├── player
                    │   │   └── FTHelperModule.java
                    │   └── render
                    │       └── TargetESPModule.java
                    └── ui
                        ├── NebulaeAddonSettingsGui.java
                        └── widget
                            ├── CycleButton.java
                            ├── FloatSlider.java
                            └── ToggleButton.java
```

## Building

The add-on can be packaged with your preferred build tool. The sources do not
rely on the Gradle configuration of the main client, so they can be dropped into
a dedicated LabyMod 3 project if desired.

