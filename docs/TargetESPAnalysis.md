# TargetESP Module Overview

This document summarises the behaviour of the original `beame.components.modules.render.TargetESP`
module and the supporting resources that it relies on.

## Runtime Flow

1. **Activation logic** – The module listens for `Render2DEvent` and `Render3DLastEvent`
   instances while the Aura combat module is enabled. It animates an `alpha` value so the
   effect fades in when Aura has a current target and fades out when the target disappears.
2. **2D rendering (`draw2DSoulsMarker`)** – Projects the target's head position into screen
   space using `W2S.project(...)`. A rotating textured quad is then drawn on the HUD using
   `ClientHandler.drawImage(...)`. Two textures are supported: `night/image/target/Quad.png`
   and `night/image/target/Quad2.png`.
3. **3D rendering (`draw3DSoulsMarker`)** – Places particles around the target using a trio
   of nested loops. The loops rely on the player's camera rotation and draw billboarded quads
   textured with `night/image/glow.png`. An alternative branch renders a vertical coloured
   circle around the entity. Both variants obtain their base colour from the client's theme
   manager and optionally tint red while the entity is hurt.

## Utility Dependencies

| Utility | Role inside TargetESP |
| --- | --- |
| `beame.util.animation.DecelerateAnimation` | Smoothly eases the alpha transition in and out.
| `beame.util.animation.AnimationMath` | Provides the fast interpolation helper used for the
  simple `alphaState` fade that drives the ghost trail transparency.
| `beame.util.color.ColorUtils` | Handles RGBA composition, interpolating between theme colours
  and converting the hurt tint into packed integers.
| `beame.util.math.MathUtil` | Supplies interpolation helpers for entity position smoothing.
| `beame.util.render.ClientHandler` | Wraps low-level texture drawing with blend state setup.
| `beame.util.render.W2S` | Projects a 3D position into 2D screen coordinates for the HUD.

Other direct dependencies include standard Minecraft rendering classes (`MatrixStack`,
`RenderSystem`, `Tessellator`, `BufferBuilder`) for issuing the actual draw calls.

## Texture Assets

The module references the following textures that ship with the client assets:

- `assets/minecraft/night/image/target/Quad.png`
- `assets/minecraft/night/image/target/Quad2.png`
- `assets/minecraft/night/image/glow.png`

These files are re-used by the LabyMod 3 addon implementation to preserve the visual identity of
the ESP effect.
