# Target ESP textures

Store the Nebulae Target ESP textures outside of version control in this directory. Mirror the following layout:

```
resources/night/image/glow.png
resources/night/image/target/Quad.png
resources/night/image/target/Quad2.png
resources/night/image/target/glow.png
```

When packaging the addon, copy these files into `src/main/resources/night/image/` so the renderer can load them at runtime.
