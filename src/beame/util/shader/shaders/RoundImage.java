package beame.util.shader.shaders;

import beame.util.shader.IShader;

public class RoundImage implements IShader {
// leaked by itskekoff; discord.gg/sk3d VSwls1Pm

    @Override
    public String glsl() {
        return """
            uniform vec2 rectSize; // Координаты и размер прямоугольника
            uniform sampler2D textureIn; // Входная текстура
            uniform float radius, alpha; // Радиус закругления углов прямоугольника и прозрачность

            // Создаем функцию для определения расстояния от текущей позиции до края прямоугольника
            float roundedSDF(vec2 centerPos, vec2 size, float radius) {
                return length(max(abs(centerPos) - size, 0.)) - radius;
            }

            void main() {
                // Определяем расстояние от текущей позиции до края прямоугольника
                float distance = roundedSDF((rectSize * .5) - (gl_TexCoord[0].st * rectSize), (rectSize * .5) - radius - 1., radius);
               \s
                // Создаем плавный переход от границы прямоугольника к прозрачной области
                float smoothedAlpha = (1.0 - smoothstep(0.0, 2.0, distance)) * alpha;

                // Создаем окончательный цвет пикселя, используя цвет из входной текстуры и плавный переход между границей прямоугольника и прозрачной областью
                gl_FragColor = vec4(texture2D(textureIn, gl_TexCoord[0].st).rgb, smoothedAlpha);
            }
            """;
    }

}

