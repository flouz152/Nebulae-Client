package beame.util.shader;

public interface IShader {
// leaked by itskekoff; discord.gg/sk3d NS2cJ3kB

    String glsl();

    default String getName() {
        return "SHADERNONAME";
    }

}
