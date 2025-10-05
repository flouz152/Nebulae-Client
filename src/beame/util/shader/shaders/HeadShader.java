package beame.util.shader.shaders;

import beame.util.shader.IShader;

public class HeadShader implements IShader {
// leaked by itskekoff; discord.gg/sk3d m7KWBev0

    @Override
    public String glsl() {
        return """ 
                #version 120
               
                 uniform sampler2D texture;
                 uniform float width;
                 uniform float height;
                 uniform float radius;
                 uniform float hurtStrength;
                 uniform float alpha;
                
                 float dstfn(vec2 p, vec2 b, float r) {
                     return length(max(abs(p) - b, 0.)) - r;
                 }
                
                 void main() {
                     vec2 tex = gl_TexCoord[0].st;
                     vec2 clippedTexCoord = vec2(
                            mix(8.0 / 64.0, 16.0 / 64.0, tex.x),
                            mix(8.0 / 64.0, 16.0 / 64.0, tex.y)
                     );
                     vec4 smpl = texture2D(texture, clippedTexCoord);
                     vec2 size = vec2(width, height);
                     vec2 pixel = tex * size;
                     vec2 centre = .5 * size;
                     float sa = smoothstep(0., 1, dstfn(centre - pixel, centre - radius - 1, radius));
                     vec4 c = mix(vec4(smpl.rgb, 1), vec4(smpl.rgb, 0), sa);
                     gl_FragColor = vec4(mix(smpl.rgb, vec3(1.0, 0.0, 0.0), hurtStrength), c.a * alpha);
                 }
                """;
    }
}
