package dev.nebulae.targetesp;

public enum TargetEspMode {
    GHOSTS(true, false),
    CIRCLE(true, false),
    SQUARE(false, true),
    NEW_SQUARE(false, true);

    private final boolean worldRenderer;
    private final boolean overlayRenderer;

    TargetEspMode(boolean worldRenderer, boolean overlayRenderer) {
        this.worldRenderer = worldRenderer;
        this.overlayRenderer = overlayRenderer;
    }

    public boolean rendersInWorld() {
        return worldRenderer;
    }

    public boolean rendersOnOverlay() {
        return overlayRenderer;
    }
}
