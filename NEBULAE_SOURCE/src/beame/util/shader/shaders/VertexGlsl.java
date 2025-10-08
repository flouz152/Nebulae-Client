package beame.util.shader.shaders;

import beame.util.shader.IShader;

public class VertexGlsl implements IShader {
// leaked by itskekoff; discord.gg/sk3d 7P8TH94v


    @Override
    public String glsl() {
        return """
                #version 120 
                 void main() {
                     // Выборка данных из текстуры во фрагментном шейдере (координаты)
                     gl_TexCoord[0] = gl_MultiTexCoord0;
                     gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
                 }
                 """;
    }
}
