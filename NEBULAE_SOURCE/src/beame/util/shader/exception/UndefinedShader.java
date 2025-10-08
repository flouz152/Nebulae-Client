package beame.util.shader.exception;

public class UndefinedShader extends Throwable {
// leaked by itskekoff; discord.gg/sk3d INXIIwAk

    private final String shader;

    @Override
    public String getMessage() {
        return shader;
    }

    public UndefinedShader(String shader) {
        this.shader =  shader;
    }

}
