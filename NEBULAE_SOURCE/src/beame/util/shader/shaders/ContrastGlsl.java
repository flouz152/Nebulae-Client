package beame.util.shader.shaders;

import beame.util.shader.IShader;

public class ContrastGlsl implements IShader {
// leaked by itskekoff; discord.gg/sk3d RNn3yCar

    @Override
    public String glsl() {
        return """
                #version 120

                uniform sampler2D texture;
                uniform float contrast;


                void main()
                {
                    vec4 color = texture2D(texture, gl_TexCoord[0].st);
                    gl_FragColor = vec4(vec3(mix(0, color.r, contrast),mix(0, color.g, contrast),mix(0, color.b, contrast)), 1);
                }
                """;
    }

}
