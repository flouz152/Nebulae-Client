package beame.util.shader;

import beame.util.shader.shaders.*;
import lombok.Getter;

public class Shaders {
// leaked by itskekoff; discord.gg/sk3d J72zRlaA
    @Getter
    private static Shaders Instance = new Shaders();
    @Getter
    private IShader vertex = new VertexGlsl();
    @Getter
    private IShader rounded = new RoundedGlsl();
    @Getter
    private IShader roundedout = new RoundedOutGlsl();
    @Getter
    private IShader smooth = new SmoothGlsl();
    @Getter
    private IShader white = new WhiteGlsl();
    @Getter
    private IShader alpha = new AlphaGlsl();
    @Getter
    private IShader gaussianbloom = new GaussianBloomGlsl();
    @Getter
    private IShader kawaseUp = new KawaseUpGlsl();
    @Getter
    private IShader kawaseDown = new KawaseDownGlsl();
    @Getter
    private IShader outline = new OutlineGlsl();
    @Getter
    private IShader contrast = new ContrastGlsl();
    @Getter
    private IShader mask = new MaskGlsl();
    @Getter
    private IShader roundedgradient = new RoundedGradient();
    @Getter
    private IShader mainmenu = new MainMenuGlsl();
    @Getter
    private IShader roundimage = new RoundImage();
    @Getter
    private IShader blur = new BlurGlsl();
    @Getter
    private IShader headshader = new HeadShader();
}
