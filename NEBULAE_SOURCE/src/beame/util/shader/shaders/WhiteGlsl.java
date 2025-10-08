package beame.util.shader.shaders;

import beame.util.shader.IShader;

public class WhiteGlsl implements IShader {
// leaked by itskekoff; discord.gg/sk3d 3yRhm6DE

    @Override
    public String glsl() {
        return """
                #version 120

                uniform sampler2D texture;
                uniform float state;  

                void main() {
                    vec3 sum = texture2D(texture, gl_TexCoord[0].st).rgb;
                    
                    float color = (sum.r + sum.g + sum.b) / 3;

                    gl_FragColor = vec4(mix(sum, vec3(color), state), 1);
                }
                    """;
    }

}
