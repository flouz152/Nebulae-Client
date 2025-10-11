# Target ESP textures

Store the Nebulae Target ESP textures outside of version control in this directory. When you are ready to build the addon, copy
the PNGs into the addon resources so Minecraft can locate them. The renderer expects the vanilla Nebulae layout:

```
src/main/resources/assets/minecraft/night/image/glow.png
src/main/resources/assets/minecraft/night/image/target/Quad.png
src/main/resources/assets/minecraft/night/image/target/Quad2.png
src/main/resources/assets/minecraft/night/image/target/glow.png
```

Minecraft will automatically resolve these `night/image/...` resource locations at runtime. Keep the actual PNG files out of
version control; only copy them into your working tree.

## Target HUD assets

The Target HUD does not require any custom PNGs. It renders vanilla Minecraft item icons, player heads, and text using the
standard game resources, so no additional textures need to be supplied for it to function.
