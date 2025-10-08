package beame.util.shader.shaders;

import beame.util.shader.IShader;

public class MainMenuGlsl implements IShader {
// leaked by itskekoff; discord.gg/sk3d AiyTnwFv

    @Override
    public String glsl() {
        return """
                #ifdef GL_ES
                precision mediump float;
                #endif
                                
                #extension GL_OES_standard_derivatives : enable
                                
                uniform vec2      resolution;
                uniform float     time;
                                
                uniform float     clr1;
                uniform float     clr2;
                uniform float     clr3;
                uniform float     clr4;
                uniform float     clr5;
                uniform float     clr6;
                                
                                
                float rand(vec2 n) {
                    return fract(cos(dot(n, vec2(16.9898, 10.1414))) * 93758.5453);
                }
                                
                float noise(vec2 n) {
                    const vec2 d = vec2(0.0, 1.0);
                    vec2 b = floor(n), f = smoothstep(vec2(0.0), vec2(1.0), fract(n));
                    return mix(mix(rand(b), rand(b + d.yx), f.x), mix(rand(b + d.xy), rand(b + d.yy), f.x), f.y);
                }
                                
                float fbm(vec2 n) {
                    float total = 0.0, amplitude = 1.0;
                    for (int i = 0; i < 10; i++) {
                        total += noise(n) * amplitude;
                        n += n;
                        amplitude *= 0.3;
                    }
                    return total;
                }
                                
                void main() {
                    vec3 c1 = vec3(clr1, clr2, clr3);
                    vec3 c2 = vec3(clr4, clr5, clr6);
                                
                    vec2 p = gl_FragCoord.xy * 7.0 / resolution.xx;
                    float q = fbm(p - time * 0.3);
                    vec2 r = vec2(fbm(p + q + time * 1.0 - p.x - p.y), fbm(p + q - time * 1.0));
                    vec3 c = mix(c1, c2, fbm(p + r));
                    gl_FragColor = vec4(c * cos(0.0 * gl_FragCoord.y / resolution.y), 1.0);
                    gl_FragColor.w = 0.8;
                }
                """;
    }
}
