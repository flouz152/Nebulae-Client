# Nebulae LabyMod 3 Addon

This directory contains a standalone LabyMod 3 addon bundle that packages the leaked
`FTHelper` and `TargetESP` modules together with their Nebulae client dependencies.
The original module sources are copied verbatim so they can be reused without any
modifications inside a vanilla LabyMod environment.

The addon exposes a very small Minecraft GUI for each module so they can be toggled
and configured without the full Nebulae client. The implementation intentionally
keeps the UI minimalistic: every screen focuses on a single module, renders a list
of its Boolean and slider settings, and provides buttons for enabling the module or
jumping to useful in-game shortcuts.

## Structure

```
labymod3-addon/
├── README.md
├── build.gradle.kts         # Minimal Gradle build script for the addon
├── settings.gradle.kts
└── src
    └── main
        ├── java
        │   ├── beame/…      # Nebulae sources copied verbatim (FTHelper, TargetESP, deps)
        │   └── nebulae/addon # Lightweight addon bootstrap & GUIs
        └── resources
            └── addon.json   # LabyMod addon metadata
```

The copied sources live under the same package names, so the addon code can interact
with them without any shims or refactors. Only the addon bootstrap and GUI classes in
`nebulae.addon` are new.

## Building

The addon can be compiled with Gradle:

```bash
cd labymod3-addon
./gradlew build
```

The resulting jar will be created in `labymod3-addon/build/libs` and can be dropped
into the `LabyMod-3/addons` folder.
