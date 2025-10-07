package beame.util.shaderExcellent.glsl;


import beame.util.shaderExcellent.IShader;

public class VertexGlsl implements IShader {
// leaked by itskekoff; discord.gg/sk3d sYYxEnHZ


    @Override
    public String shader() {
        return """
                #version 120
                                
                varying vec4 VertexColor;
                 
                void main() {
                    gl_TexCoord[0] = gl_MultiTexCoord0;
                    VertexColor = gl_Color;
                    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
                }""";
    }
}
