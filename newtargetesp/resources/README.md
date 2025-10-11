# Target ESP textures

Store the Nebulae Target ESP textures outside of version control in this directory. When you are ready to build the addon, copy
the PNGs into the addon resources so Minecraft can locate them. The renderer accepts either of the following classpath layouts:

```
src/main/resources/assets/minecraft/night/image/glow.png
src/main/resources/assets/minecraft/night/image/target/Quad.png
src/main/resources/assets/minecraft/night/image/target/Quad2.png
src/main/resources/assets/minecraft/night/image/target/glow.png
```

or

```
src/main/resources/night/image/glow.png
src/main/resources/night/image/target/Quad.png
src/main/resources/night/image/target/Quad2.png
src/main/resources/night/image/target/glow.png
```

Placing the textures under `assets/minecraft/` matches Nebulae's original packaging, but a flat `night/image/` folder is also
supported if you are testing locally. Keep the actual PNG files out of version control; only copy them into your working tree.

## Target HUD assets

The Target HUD does not require any custom PNGs. It renders vanilla Minecraft item icons, player heads, and text using the
standard game resources, so no additional textures need to be supplied for it to function.
