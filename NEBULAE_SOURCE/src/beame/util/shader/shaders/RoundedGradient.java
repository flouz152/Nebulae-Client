package beame.util.shader.shaders;

import beame.util.shader.IShader;

public class RoundedGradient implements IShader {
// leaked by itskekoff; discord.gg/sk3d MHaGFArm

    @Override
    public String glsl() {
        return """
                #version 120
                
                uniform vec2 location, rectSize;
                uniform vec4 color0;
                uniform vec4 color1;
                uniform vec4 color2;
                uniform vec4 color3;
                uniform float radius;
                uniform bool blur;
                
                float signedDistanceField(vec2 p, vec2 b, float r) {
                    vec2 q = abs(p) - b + r;
                    return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - r;
                }
                
                float roundSDF(vec2 p, vec2 b, float r) {
                    return length(max(abs(p), 0.0)) - r;
                }
                
                vec3 createGradient(vec2 pos) {
                    return mix(mix(color0.rgb, color1.rgb, pos.y), mix(color2.rgb, color3.rgb, pos.y), pos.x);
                }
                
                void main() {
                    vec2 rectHalf = rectSize * .5;
                    float smoothedAlpha = (1.0-smoothstep(0.0, 1.0, roundSDF(rectHalf - (gl_TexCoord[0].st * rectSize), rectHalf - radius - 1., radius))) * color0.a;
                    float sdf = signedDistanceField(rectHalf - gl_TexCoord[0].st * rectSize, rectHalf - 1.0, radius);
                    gl_FragColor = vec4(createGradient(gl_TexCoord[0].st), (1.0 - smoothstep(0.0, 1.0, sdf)) * color0.a);
                }
                """;
    }
}
