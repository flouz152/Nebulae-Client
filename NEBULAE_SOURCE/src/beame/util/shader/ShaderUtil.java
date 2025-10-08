package beame.util.shader;

import beame.util.IMinecraft;
import beame.util.shader.exception.UndefinedShader;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.MainWindow;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.math.vector.Matrix4f;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.CallbackI;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import static net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_TEX;
import static org.lwjgl.opengl.ARBShaderObjects.*;
import static org.lwjgl.opengl.ARBShaderObjects.glUniform1iARB;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class ShaderUtil implements IMinecraft {
// leaked by itskekoff; discord.gg/sk3d utBBoTWv
    private final int programID;

    public static ShaderUtil rounded = new ShaderUtil("rounded");
    public static ShaderUtil mainmenu = new ShaderUtil("mainmenu");
    public static ShaderUtil roundedout = new ShaderUtil("roundedout");
    public static ShaderUtil smooth = new ShaderUtil("smooth");
    public static ShaderUtil white = new ShaderUtil("white");
    public static ShaderUtil alpha = new ShaderUtil("alpha");
    public static ShaderUtil KAWASE_UP = new ShaderUtil("kawaseUp2");
    public static ShaderUtil KAWASE_DOWN = new ShaderUtil("kawaseDown2");
    public static ShaderUtil kawaseUp = new ShaderUtil("kawaseUp");
    public static ShaderUtil kawaseDown = new ShaderUtil("kawaseDown");
    public static ShaderUtil outline = new ShaderUtil("outline");
    public static ShaderUtil contrast = new ShaderUtil("contrast");
    public static ShaderUtil mask = new ShaderUtil("mask");
    public static ShaderUtil roundedgradient = new ShaderUtil("roundedgradient");
    public static ShaderUtil roundimage = new ShaderUtil("roundimage");
    public static ShaderUtil headshader = new ShaderUtil("headshader");
    //public static ShaderUtil ROUND_RECT_SHADER = new ShaderUtil("roundRect");

    public ShaderUtil(String fragmentShaderLoc) {
        programID = ARBShaderObjects.glCreateProgramObjectARB();

        try {
            // �������� ������������ �������
            int fragmentShaderID = switch (fragmentShaderLoc) {
                case "mainmenu" -> createShader(Shaders.getInstance().getMainmenu(), GL_FRAGMENT_SHADER);
                case "smooth" -> createShader(Shaders.getInstance().getSmooth(), GL_FRAGMENT_SHADER);
                case "white" -> createShader(Shaders.getInstance().getWhite(), GL_FRAGMENT_SHADER);
                case "rounded" -> createShader(Shaders.getInstance().getRounded(), GL_FRAGMENT_SHADER);
                case "roundedout" -> createShader(Shaders.getInstance().getRoundedout(), GL_FRAGMENT_SHADER);
                case "roundedgradient" -> createShader(Shaders.getInstance().getRoundedgradient(), GL_FRAGMENT_SHADER);
                case "bloom" -> createShader(Shaders.getInstance().getGaussianbloom(), GL_FRAGMENT_SHADER);
                case "kawaseUp" -> createShader(Shaders.getInstance().getKawaseUp(), GL_FRAGMENT_SHADER);
                case "kawaseDown" -> createShader(Shaders.getInstance().getKawaseDown(), GL_FRAGMENT_SHADER);
                case "kawaseUp2" -> createShader(new ByteArrayInputStream(KAWASE_UP0.getBytes()), GL_FRAGMENT_SHADER);
                case "kawaseDown2" -> createShader(new ByteArrayInputStream(KAWASE_DOWN0.getBytes()), GL_FRAGMENT_SHADER);
                case "alpha" -> createShader(Shaders.getInstance().getAlpha(), GL_FRAGMENT_SHADER);
                case "outline" -> createShader(Shaders.getInstance().getOutline(), GL_FRAGMENT_SHADER);
                case "contrast" -> createShader(Shaders.getInstance().getContrast(), GL_FRAGMENT_SHADER);
                case "mask" -> createShader(Shaders.getInstance().getMask(), GL_FRAGMENT_SHADER);
                case "roundimage" -> createShader(Shaders.getInstance().getRoundimage(), GL_FRAGMENT_SHADER);
                case "blur" -> createShader(Shaders.getInstance().getBlur(), GL_FRAGMENT_SHADER);
                case "headshader" -> createShader(Shaders.getInstance().getHeadshader(), GL_FRAGMENT_SHADER);
                default ->
                        throw new UndefinedShader(fragmentShaderLoc);
            };
            ARBShaderObjects.glAttachObjectARB(programID, fragmentShaderID);

            ARBShaderObjects.glAttachObjectARB(programID,
                    createShader(Shaders.getInstance().getVertex(), GL_VERTEX_SHADER));
            ARBShaderObjects.glLinkProgramARB(programID);
        } catch (UndefinedShader exception) {
            exception.fillInStackTrace();
            System.out.println("������ ��� ��������: " + fragmentShaderLoc);
        }
    }


    String KAWASE_UP0 = """
            #version 120

            uniform sampler2D inTexture, textureToCheck;
            uniform vec2 halfPixel, offset, resolution;
            uniform int check;

            void main() {
                vec2 uv = vec2(gl_FragCoord.xy / resolution);
                vec4 sum = texture2D(inTexture, uv + vec2(-halfPixel.x * 2, 0) * offset);
                sum += texture2D(inTexture, uv + vec2(-halfPixel.x, halfPixel.y) * offset) * 2;
                sum += texture2D(inTexture, uv + vec2(0, halfPixel.y * 2) * offset);
                sum += texture2D(inTexture, uv + vec2(halfPixel.x, halfPixel.y) * offset) * 2;
                sum += texture2D(inTexture, uv + vec2(halfPixel.x * 2, 0) * offset);
                sum += texture2D(inTexture, uv + vec2(halfPixel.x, -halfPixel.y) * offset) * 2;
                sum += texture2D(inTexture, uv + vec2(0, -halfPixel.y * 2) * offset);
                sum += texture2D(inTexture, uv + vec2(-halfPixel.x, -halfPixel.y) * offset) * 2;
                gl_FragColor = vec4(sum.rgb / 12, mix(1, texture2D(textureToCheck, gl_TexCoord[0].st).a, check));
            }
            """;

    String KAWASE_DOWN0 = """
            #version 120

            uniform sampler2D inTexture;
            uniform vec2 offset, halfPixel, resolution;

            void main() {
                vec2 uv = vec2(gl_FragCoord.xy / resolution);
                vec4 sum = texture2D(inTexture, gl_TexCoord[0].st) * 4;
                sum += texture2D(inTexture, uv - halfPixel.xy * offset);
                sum += texture2D(inTexture, uv + halfPixel.xy * offset);
                sum += texture2D(inTexture, uv + vec2(halfPixel.x, -halfPixel.y) * offset);
                sum += texture2D(inTexture, uv - vec2(halfPixel.x, -halfPixel.y) * offset);
                gl_FragColor = vec4(sum.rgb * 0.125, 1);
            }
            """;

    public static Framebuffer createFrameBuffer(Framebuffer framebuffer) {
        return createFrameBuffer(framebuffer, false);
    }

    public static boolean needsNewFramebuffer(Framebuffer framebuffer) {
        return framebuffer == null || framebuffer.framebufferWidth != mc.getMainWindow().getWidth()
                || framebuffer.framebufferHeight != mc.getMainWindow().getHeight();
    }

    public int getUniform(String name) {
        return ARBShaderObjects.glGetUniformLocationARB(programID, name);
    }

    public static Framebuffer createFrameBuffer(Framebuffer framebuffer, boolean depth) {
        try {
            if (needsNewFramebuffer(framebuffer)) {
                if (framebuffer != null) {
                    framebuffer.deleteFramebuffer();
                }
                return new Framebuffer(mc.getMainWindow().getWidth(), mc.getMainWindow().getHeight(), depth, false);
            }
            return framebuffer;
        } catch (Exception ex) {
            return null;
        }
    }

//    public static void drawQuads(float x, float y, float width, float height) {
//        glBegin(GL_QUADS);
//        glTexCoord2f(0, 0);
//        glVertex2f(x, y);
//        glTexCoord2f(0, 1);
//        glVertex2f(x, y + height);
//        glTexCoord2f(1, 1);
//        glVertex2f(x + width, y + height);
//        glTexCoord2f(1, 0);
//        glVertex2f(x + width, y);
//        glEnd();
//    }

    public static void drawQuadss(MatrixStack matrixStack, double x, double y, double width, double height) {
        buffer.begin(GL_POLYGON, POSITION_TEX);
        {
            Matrix4f matrix = matrixStack.getLast().getMatrix();
            buffer.pos(matrix.adjugateAndDet(), (float) x, (float) y).tex(0, 0).endVertex();
            buffer.pos(matrix.adjugateAndDet(), (float) x, (float) (y + height)).tex(0, 1).endVertex();
            buffer.pos(matrix.adjugateAndDet(), (float) (x + width), (float) (y + height)).tex(1, 1).endVertex();
            buffer.pos(matrix.adjugateAndDet(), (float) (x + width), (float) y).tex(1, 0).endVertex();
        }
        tessellator.draw();
    }

    public static void drawQuads() {
        MainWindow sr = mc.getMainWindow();
        float width = (float) sr.getScaledWidth();
        float height = (float) sr.getScaledHeight();
        glBegin(GL_QUADS);
        glTexCoord2f(0, 1);
        glVertex2f(0, 0);
        glTexCoord2f(0, 0);
        glVertex2f(0, height);
        glTexCoord2f(1, 0);
        glVertex2f(width, height);
        glTexCoord2f(1, 1);
        glVertex2f(width, 0);
        glEnd();
    }

    public Framebuffer setupBuffer(Framebuffer frameBuffer) {
        if (frameBuffer.framebufferWidth != mc.getMainWindow().getWidth()
                || frameBuffer.framebufferHeight != mc.getMainWindow().getHeight())
            frameBuffer.resize(Math.max(1, mc.getMainWindow().getWidth()), Math.max(1, mc.getMainWindow().getHeight()),
                    false);
        else
            frameBuffer.framebufferClear(false);
        frameBuffer.setFramebufferColor(0.0f, 0.0f, 0.0f, 0.0f);

        return frameBuffer;
    }


    /**
     * ����������� ������� � ��������� OpenGL
     */
    public void attach() {
        ARBShaderObjects.glUseProgramObjectARB(programID);
    }

    /**
     * ���������� ������� �� ��������� OpenGL
     */
    public void detach() {
        glUseProgram(0);
    }

    /**
     * ��������� �������� uniform ����������
     *
     * @param name
     * @param args
     */
    public void setUniform(String name, float... args) {
        int loc = ARBShaderObjects.glGetUniformLocationARB(programID, name);
        switch (args.length) {
            case 1 -> ARBShaderObjects.glUniform1fARB(loc, args[0]);
            case 2 -> ARBShaderObjects.glUniform2fARB(loc, args[0], args[1]);
            case 3 -> ARBShaderObjects.glUniform3fARB(loc, args[0], args[1], args[2]);
            case 4 -> ARBShaderObjects.glUniform4fARB(loc, args[0], args[1], args[2], args[3]);
            default ->
                    throw new IllegalArgumentException("������������ ���������� ���������� ��� uniform '" + name + "'");
        }
    }

//    public void setUniform(String name, int... args) {
//        int loc = ARBShaderObjects.glGetUniformLocationARB(programID, name);
//        switch (args.length) {
//            case 1 -> glUniform1iARB(loc, args[0]);
//            case 2 -> glUniform2iARB(loc, args[0], args[1]);
//            case 3 -> glUniform3iARB(loc, args[0], args[1], args[2]);
//            case 4 -> glUniform4iARB(loc, args[0], args[1], args[2], args[3]);
//            default ->
//                    throw new IllegalArgumentException("������������ ���������� ���������� ��� uniform '" + name + "'");
//        }
//    }

    public void setUniform(String name, int... args) {
        int loc = glGetUniformLocation(programID, name);
        switch (args.length) {
            case 1 -> glUniform1iARB(loc, args[0]);
            case 2 -> glUniform2iARB(loc, args[0], args[1]);
            case 3 -> glUniform3iARB(loc, args[0], args[1], args[2]);
            case 4 -> glUniform4iARB(loc, args[0], args[1], args[2], args[3]);
        }
    }

    public void setUniformf(String var1, float... var2) {
        int var3 = ARBShaderObjects.glGetUniformLocationARB(this.programID, var1);
        switch (var2.length) {
            case 1 -> ARBShaderObjects.glUniform1fARB(var3, var2[0]);
            case 2 -> ARBShaderObjects.glUniform2fARB(var3, var2[0], var2[1]);
            case 3 -> ARBShaderObjects.glUniform3fARB(var3, var2[0], var2[1], var2[2]);
            case 4 -> ARBShaderObjects.glUniform4fARB(var3, var2[0], var2[1], var2[2], var2[3]);
        }
    }

    public void setUniformi(String var1, int var2) {
        int var3 = ARBShaderObjects.glGetUniformLocationARB(this.programID, var1);
        ARBShaderObjects.glUniform1iARB(var3, var2);
    }

    public void setUniformf(String var1, float var2) {
        int var3 = ARBShaderObjects.glGetUniformLocationARB(this.programID, var1);
        ARBShaderObjects.glUniform1fARB(var3, var2);
    }

    public void setUniformf(String var1, double... var2) {
        int var3 = ARBShaderObjects.glGetUniformLocationARB(this.programID, var1);
        switch (var2.length) {
            case 1 -> ARBShaderObjects.glUniform1fARB(var3, (float) var2[0]);
            case 2 -> ARBShaderObjects.glUniform2fARB(var3, (float) var2[0], (float) var2[1]);
            case 3 -> ARBShaderObjects.glUniform3fARB(var3, (float) var2[0], (float) var2[1], (float) var2[2]);
            case 4 -> ARBShaderObjects.glUniform4fARB(var3, (float) var2[0], (float) var2[1], (float) var2[2],
                    (float) var2[3]);
        }
    }
    private int createShader(InputStream inputStream, int shaderType) {
        int shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType);
        ARBShaderObjects.glShaderSourceARB(shader, readInputStream(inputStream));
        ARBShaderObjects.glCompileShaderARB(shader);
        if (GL20.glGetShaderi(shader, 35713) == 0) {
            System.out.println(GL20.glGetShaderInfoLog(shader, 4096));
            throw new IllegalStateException(String.format("Shader (%s) failed to compile!", shaderType));
        }
        return shader;
    }
    private int createShader(IShader glsl, int shaderType) {
        int shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType);
        ARBShaderObjects.glShaderSourceARB(shader, readInputStream(new ByteArrayInputStream(glsl.glsl().getBytes())));
        ARBShaderObjects.glCompileShaderARB(shader);
        if (GL20.glGetShaderi(shader, 35713) == 0) {
            System.out.println(GL20.glGetShaderInfoLog(shader, 4096));
            throw new IllegalStateException(String.format("Shader (%s) failed to compile!", shaderType));
        }
        return shader;
    }

    public String readInputStream(InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream)).lines()
                .map(line -> line + '\n')
                .collect(Collectors.joining());
    }

}