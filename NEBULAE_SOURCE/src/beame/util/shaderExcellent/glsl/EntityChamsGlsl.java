package beame.util.shaderExcellent.glsl;

import beame.util.shaderExcellent.IShader;

public class EntityChamsGlsl implements IShader {
// leaked by itskekoff; discord.gg/sk3d QKastQzW
    @Override
    public String shader() {
        return """
                #version 120
                                        
                uniform vec2 location, rectSize;
                uniform sampler2D tex;
                uniform vec4 color;
                                        
                void main() {
                    vec2 coords = (gl_FragCoord.xy - location) / rectSize;
                    float texColorAlpha = texture2D(tex, gl_TexCoord[0].st).a;
                    gl_FragColor = vec4(color.rgb, texColorAlpha);
                }""";
    }
}
