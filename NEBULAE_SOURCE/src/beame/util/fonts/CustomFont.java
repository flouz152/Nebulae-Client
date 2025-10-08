package beame.util.fonts;


import beame.util.color.ColorUtils;
import com.google.common.base.Preconditions;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.Getter;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;

import java.awt.*;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static net.optifine.render.GLConst.*;
import static org.lwjgl.opengl.GL11C.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11C.GL_SRC_ALPHA;


public class CustomFont {
// leaked by itskekoff; discord.gg/sk3d OcIk7tv6

    /**
     * Current X coordinate at which to draw the next character.
     */
    private float posX;
    /**
     * Current Y coordinate at which to draw the next character.
     */
    private float posY;
    /**
     * Array of RGB triplets defining the 16 standard chat colors followed by 16
     * darker version of the same colors for drop shadows.
     */
    private final int[] colorCode = new int[32];
    /**
     * Set if the "l" style (bold) is active in currently rendering string
     */
    private boolean boldStyle;
    /**
     * Set if the "o" style (italic) is active in currently rendering string
     */
    private boolean italicStyle;
    /**
     * Set if the "n" style (underlined) is active in currently rendering string
     */
    private boolean underlineStyle;
    /**
     * Set if the "m" style (strikethrough) is active in currently rendering string
     */
    private boolean strikethroughStyle;

    private final GlyphPage regularGlyphPage;
    private final GlyphPage boldGlyphPage;
    private final GlyphPage italicGlyphPage;
    private final GlyphPage boldItalicGlyphPage;

    final Map<String, String> utfENSymbolsMap = IntStream.range(0,
                    "\u0410 А \u0411 Б \u0412 В \u0413 Г \u0414 Д \u0415 Е \u0401 Ё \u0416 Ж \u0417 З \u0418 И \u0419 Й \u041A К \u041B Л \u041C М \u041D Н \u041E О \u041F П \u0420 Р \u0421 С \u0422 Т \u0423 У \u0424 Ф \u0425 Х \u0426 Ц \u0427 Ч \u0428 Ш \u0429 Щ \u042A Ъ \u042B Ы \u042C Ь \u042D Э \u042E Ю \u042F Я \u0430 а \u0431 б \u0432 в \u0433 г \u0434 д \u0435 е \u0451 ё \u0436 ж \u0437 з \u0438 и \u0439 й \u043A к \u043B л \u043C м \u043D н \u043E о \u043F п \u0440 р \u0441 с \u0442 т \u0443 у \u0444 ф \u0445 х \u0446 ц \u0447 ч \u0448 ш \u0449 щ \u044A ъ \u044B ы \u044C ь \u044D э \u044E ю \u044F я".split(" ").length)
            .filter(i -> i % 2 == 0)
            .boxed()
            .collect(Collectors.toMap(i ->
                            "\u0410 А \u0411 Б \u0412 В \u0413 Г \u0414 Д \u0415 Е \u0401 Ё \u0416 Ж \u0417 З \u0418 И \u0419 Й \u041A К \u041B Л \u041C М \u041D Н \u041E О \u041F П \u0420 Р \u0421 С \u0422 Т \u0423 У \u0424 Ф \u0425 Х \u0426 Ц \u0427 Ч \u0428 Ш \u0429 Щ \u042A Ъ \u042B Ы \u042C Ь \u042D Э \u042E Ю \u042F Я \u0430 а \u0431 б \u0432 в \u0433 г \u0434 д \u0435 е \u0451 ё \u0436 ж \u0437 з \u0438 и \u0439 й \u043A к \u043B л \u043C м \u043D н \u043E о \u043F п \u0440 р \u0441 с \u0442 т \u0443 у \u0444 ф \u0445 х \u0446 ц \u0447 ч \u0448 ш \u0449 щ \u044A ъ \u044B ы \u044C ь \u044D э \u044E ю \u044F я".split(" ")[i],
                    i ->
                            "\u0410 А \u0411 Б \u0412 В \u0413 Г \u0414 Д \u0415 Е \u0401 Ё \u0416 Ж \u0417 З \u0418 И \u0419 Й \u041A К \u041B Л \u041C М \u041D Н \u041E О \u041F П \u0420 Р \u0421 С \u0422 Т \u0423 У \u0424 Ф \u0425 Х \u0426 Ц \u0427 Ч \u0428 Ш \u0429 Щ \u042A Ъ \u042B Ы \u042C Ь \u042D Э \u042E Ю \u042F Я \u0430 а \u0431 б \u0432 в \u0433 г \u0434 д \u0435 е \u0451 ё \u0436 ж \u0437 з \u0438 и \u0439 й \u043A к \u043B л \u043C м \u043D н \u043E о \u043F п \u0440 р \u0441 с \u0442 т \u0443 у \u0444 ф \u0445 х \u0446 ц \u0447 ч \u0448 ш \u0449 щ \u044A ъ \u044B ы \u044C ь \u044D э \u044E ю \u044F я".split(" ")[i + 1]));

    public CustomFont(GlyphPage regularGlyphPage, GlyphPage boldGlyphPage, GlyphPage italicGlyphPage,
                      GlyphPage boldItalicGlyphPage) {
        this.regularGlyphPage = regularGlyphPage;
        this.boldGlyphPage = boldGlyphPage;
        this.italicGlyphPage = italicGlyphPage;
        this.boldItalicGlyphPage = boldItalicGlyphPage;

        for (int i = 0; i < 32; ++i) {
            int j = (i >> 3 & 1) * 85;
            int k = (i >> 2 & 1) * 170 + j;
            int l = (i >> 1 & 1) * 170 + j;
            int i1 = (i & 1) * 170 + j;

            if (i == 6) {
                k += 85;
            }

            if (i >= 16) {
                k /= 4;
                l /= 4;
                i1 /= 4;
            }

            this.colorCode[i] = (k & 255) << 16 | (l & 255) << 8 | i1 & 255;
        }
    }
    public static CustomFont create(String file, float size, boolean bold, boolean italic, boolean boldItalic) {

        Font font = null;

        try {
            @Cleanup InputStream in = Preconditions.checkNotNull(CustomFont.class.getResourceAsStream("/assets/minecraft/night/font/" + file), "Font resource is null");
            font = Font.createFont(Font.TRUETYPE_FONT, in)
                    .deriveFont(Font.PLAIN, size);
        } catch (Exception e) {
            e.printStackTrace();
        }

        GlyphPage regularPage;

        regularPage = new GlyphPage(font, true, true);
        regularPage.generateGlyphPage();
        regularPage.setupTexture();

        GlyphPage boldPage = regularPage;
        GlyphPage italicPage = regularPage;
        GlyphPage boldItalicPage = regularPage;

        try {
            if (bold) {
                @Cleanup InputStream in = Preconditions.checkNotNull(CustomFont.class.getResourceAsStream("/assets/minecraft/night/font/" + file), "Font resource is null");
                boldPage = new GlyphPage(
                        Font.createFont(Font.TRUETYPE_FONT, in)
                                .deriveFont(Font.BOLD, size),
                        true, true);

                boldPage.generateGlyphPage();
                boldPage.setupTexture();
            }

            if (italic) {
                @Cleanup InputStream in = Preconditions.checkNotNull(CustomFont.class.getResourceAsStream("/assets/minecraft/night/font/" + file), "Font resource is null");
                italicPage = new GlyphPage(
                        Font.createFont(Font.TRUETYPE_FONT, in)
                                .deriveFont(Font.ITALIC, size),
                        true, true);

                italicPage.generateGlyphPage();
                italicPage.setupTexture();
            }

            if (boldItalic) {
                @Cleanup InputStream in = Preconditions.checkNotNull(CustomFont.class.getResourceAsStream("/assets/minecraft/night/font/" + file), "Font resource is null");

                boldItalicPage = new GlyphPage(
                        Font.createFont(Font.TRUETYPE_FONT, in)
                                .deriveFont(Font.BOLD | Font.ITALIC, size),
                        true, true);

                boldItalicPage.generateGlyphPage();
                boldItalicPage.setupTexture();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new CustomFont(regularPage, boldPage, italicPage, boldItalicPage);
    }

    public int drawString(MatrixStack matrices, String text, float x, float y, int color, int letterMax) {
        if (ColorUtils.a(color) < 10) return 0;
        this.resetStyles();
        int i = this.renderString(matrices, text, x * 2.0f , y * 2.0f, color, letterMax);
        return i;
    }
    public int drawString(String text, float x, float y, int color, int letterMax) {
        MatrixStack matrices = new MatrixStack();
        if (ColorUtils.a(color) < 10) return 0;
        this.resetStyles();
        int i = this.renderString(matrices, text, x * 2.0f , y * 2.0f, color, letterMax);
        return i;
    }

    private int renderString(MatrixStack matrices, String text, float x, float y, int color, int letterMax) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        float posX = x;
        if ((color & -67108864) == 0) {
            color |= -16777216;
        }
        float alpha = (float) (color >> 24 & 255) / 255.0F;
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;
        int totalWidth = 0;
        matrices.push();
        matrices.scale(0.5f, 0.5F, 0.5F);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.enableTexture();

        GlyphPage glyphPage = getCurrentGlyphPage();
        glyphPage.bindTexture();

        for (int index = 0; index < text.length(); index++) {
            char c = text.charAt(index);
            float currentAlpha;
            if (index < letterMax) {
                currentAlpha = alpha;
            } else {
                float transitionIndex = index - letterMax;
                currentAlpha = Math.max(0.0f, alpha * (1.0f - transitionIndex / 3.0f));
            }
            float width = glyphPage.drawChar(matrices, c, posX, y, red, blue, green, currentAlpha);
            totalWidth += (int) (width / 4.0f);
            posX += width;
        }

        glyphPage.unbindTexture();
        matrices.pop();

        return totalWidth;
    }

    public int drawString(String text, float x, double y, int color) {
        MatrixStack matrices = new MatrixStack();
        draw(matrices, text, x, (float)y, color, false);
        return getWidth(text);
    }

    public int drawString(MatrixStack matrix, String text, float x, double y, int color) {
        MatrixStack matrices = new MatrixStack();
        draw(matrices, text, x, (float)y, color, false);
        return getWidth(text);
    }

    public int drawString(String text, float x, float y, int color) {
        MatrixStack matrices = new MatrixStack();
        draw(matrices, text, x, y, color, false);
        return getWidth(text);
    }

    public ITextComponent drawString1(String text, float x, float y, int color) {
        MatrixStack matrices = new MatrixStack();
        draw(matrices, text, x, y, color, false);
        return ITextComponent.getTextComponentOrEmpty(text);
    }

    public int drawString(MatrixStack matrix, String text, float x, float y, int color) {
        MatrixStack matrices = new MatrixStack();
        draw(matrices, text, x, y, color, false);
        return getWidth(text);
    }

    public int drawGradientStringCenter(MatrixStack matrix, String text, float x, float y, int color1, int color2, int colorSpeed, int indexMulc) {
        drawGradientString(text, x - getWidth(text) / 2f, y, color1, color2, colorSpeed, indexMulc);
        return getWidth(text);
    }

    public int drawGradientString(String text, float x, float y, int color1, int color2, int colorSpeed, int indexMulc) {
        MatrixStack matrices = new MatrixStack();
        float offset = 0;
        for (int i = 0; i < text.length(); i++) {
            char character = text.charAt(i);
            if (character != ' ') {
                draw(matrices, String.valueOf(character), x + offset, y, ColorUtils.interpolateTwoColors((int) colorSpeed, i * indexMulc, new Color(color1), new Color(color2), false).getRGB(), false);
                offset += getWidth(String.valueOf(character)) + 0.15f;
            } else {
                offset += getWidth(" ") + 2;
            }
        }
        return getWidth(text);
    }


    public int drawGradientLong(String text, float x, float y, int color1, int color2, int colorSpeed, int indexMulc) {
        drawGradientString(text, x, y, color1, color2, colorSpeed, indexMulc);
        return getWidth(text);
    }

    public int drawLong(String text, float x, float y, int color) {
        MatrixStack matrices = new MatrixStack();
        draw(matrices, text,(long) x, (long) y, color, false);
        return getWidth(text);
    }

    public int draw(MatrixStack matrices, String text, float x, float y, int color) {
        return draw(matrices, text, x, y, color, false);
    }

    public int draw(String text, double x, double y, int color) {
        MatrixStack matrices = new MatrixStack();
        return draw(matrices, text, (float) x, (float) y, color, false);
    }

    public int draw(MatrixStack matrices, String text, double x, double y, int color) {
        return draw(matrices, text, (float) x, (float) y, color, false);
    }

    public int drawCenteredString(MatrixStack stack, String text, double x, double y, int color) {
        MatrixStack matrices = new MatrixStack();
        return draw(matrices, text, (float) x - (getWidth(text) / 2f), (float) y, color, false);
    }

    public int drawCenteredString(String text, double x, double y, int color) {
        MatrixStack matrices = new MatrixStack();
        return draw(matrices, text, (float) x - (getWidth(text) / 2f), (float) y, color, false);
    }

    public int drawCenterX(String text, double x, double y, int color) {
        MatrixStack matrices = new MatrixStack();
        return draw(matrices, text, (float) x - (getWidth(text) / 2f), (float) y, color, false);
    }


    /**
     * Draws the specified string.
     */
    public int draw(MatrixStack matrices, String text, float x, float y, int color, boolean dropShadow) {
        if (ColorUtils.a(color) < 10) return 0;

        this.resetStyles();
        int i = 0;

        if (dropShadow) {
            i = this.renderString(matrices, text, x + 0.5F, y + 0.5F, color, false);
            i = Math.max(i, this.renderString(matrices, text, x, y, color, false));
        } else {
            i = this.renderString(matrices, text, x, y, color, false);
        }

        return i;
    }


    /**
     * Render single line string by beame.setting GL color, current (posX,posY), and
     * calling renderStringAtPos()
     */
    private int renderString(MatrixStack matrices, String text, float x, float y, int color, boolean dropShadow) {
        if (text == null) {
            return 0;
        } else {
            text = replaceUtfSymbolz(text);
            if ((color & -67108864) == 0) {
                color |= -16777216;
            }

            if (dropShadow) {
                color = (color & 16579836) >> 2 | color & -16777216;
            }
            this.posX = x * 2.0f;
            this.posY = y * 2.0f;
            this.renderStringAtPos(matrices, text, false, color);
            return (int) (this.posX / 4.0f);
        }
    }

    private int renderString1(MatrixStack matrices, String text, float x, float y, int color, boolean dropShadow) {
        if (text == null) {
            return 0;
        } else {
            // text = replaceUtfSymbolz(text);
            if ((color & -67108864) == 0) {
                color |= -16777216;
            }

            if (dropShadow) {
                color = (color & 16579836) >> 2 | color & -16777216;
            }
            this.posX = x * 2.0f;
            this.posY = y * 2.0f;
            this.renderStringAtPos1(matrices, text, dropShadow, color);
            return (int) (this.posX / 4.0f);
        }
    }

    private void renderStringAtPos1(MatrixStack matrices, String text, boolean shadow, int color) {
        GlyphPage glyphPage = getCurrentGlyphPage();
        float alpha = (float) (color >> 24 & 255) / 255.0F;
        float g = (float) (color >> 16 & 255) / 255.0F;
        float h = (float) (color >> 8 & 255) / 255.0F;
        float k = (float) (color & 255) / 255.0F;

        matrices.push();

        matrices.scale(0.5f, 0.5F, 0.5F);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.enableTexture();

        glyphPage.bindTexture();

        for (int i = 0; i < text.length(); ++i) {
            char c0 = text.charAt(i);

            if (c0 == 167 && i + 1 < text.length()) {
                int i1 = "0123456789abcdefklmnor".indexOf(text.toLowerCase(Locale.ENGLISH).charAt(i + 1));

                if (i1 < 16) {
                    this.boldStyle = false;
                    this.strikethroughStyle = false;
                    this.underlineStyle = false;
                    this.italicStyle = false;

                    if (i1 < 0) {
                        i1 = 15;
                    }

                    if (shadow) {
                        i1 += 16;
                    }

                    int j1 = this.colorCode[i1];

                    g = (float) (j1 >> 16 & 255) / 255.0F;
                    h = (float) (j1 >> 8 & 255) / 255.0F;
                    k = (float) (j1 & 255) / 255.0F;
                } else if (i1 == 16) {
                } else if (i1 == 17) {
                    this.boldStyle = true;
                } else if (i1 == 18) {
                    this.strikethroughStyle = true;
                } else if (i1 == 19) {
                    this.underlineStyle = true;
                } else if (i1 == 20) {
                    this.italicStyle = true;
                } else {
                    this.boldStyle = false;
                    this.strikethroughStyle = false;
                    this.underlineStyle = false;
                    this.italicStyle = false;
                }

                ++i;
            } else {
                glyphPage = getCurrentGlyphPage();

                glyphPage.bindTexture();
                float f = glyphPage.drawChar(matrices, c0, posX, posY, g, k, h, alpha);
                RenderSystem.texParameter(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
                doDraw(matrices, f, glyphPage);
            }
        }

        glyphPage.unbindTexture();
        matrices.pop();
    }
    public String replaceUtfSymbolz(String text) {
        return text.chars()
                .mapToObj(c -> String.valueOf((char) c))
                .map(s -> utfENSymbolsMap.getOrDefault(s, s))
                .collect(Collectors.joining());
    }

    public float[] getColorFromStyle(Style style) {
        if (style.getColor() != null) {
            int color = style.getColor().getColor();
            return new float[]{
                    (color >> 16 & 255) / 255.0F,
                    (color >> 8 & 255) / 255.0F,
                    (color & 255) / 255.0F,
                    1.0F
            };
        }
        return new float[]{1.0F, 1.0F, 1.0F, 1.0F};
    }
    public void renderTextWithStyle(MatrixStack matrices, ITextComponent text, float posX, float posY) {
        matrices.push();
        matrices.scale(0.5f, 0.5F, 0.5F);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.enableTexture();

        float[] currentColor = new float[]{1.0F, 1.0F, 1.0F, 1.0F}; // Начальный цвет - белый

        for (ITextComponent part : text.getSiblings()) {
            if (part instanceof TextComponent) {
                TextComponent textComponent = (TextComponent) part;
                Style style = part.getStyle();

                float[] color = getColorFromStyle(style);

                String content = textComponent.getText();
                renderStringAtPos(matrices, content, posX, posY, color);
                posX += getWidth(content);
            }
        }
        matrices.pop();
    }

    private void renderStringAtPos(MatrixStack matrices, String text, float posX, float posY, float[] color) {
        GlyphPage glyphPage = getCurrentGlyphPage();
        glyphPage.bindTexture();

        float r = color[0];
        float g = color[1];
        float b = color[2];
        float alpha = color[3];

        for (int i = 0; i < text.length(); ++i) {
            char c = text.charAt(i);
            glyphPage.drawChar(matrices, c, posX, posY, r, g, b, alpha);
            posX += glyphPage.getWidth(c);
        }

        glyphPage.unbindTexture();
    }


    private void renderStringAtPos(MatrixStack matrices, String text, boolean shadow, int color) {
        GlyphPage glyphPage = getCurrentGlyphPage();
        float alpha = (float) (color >> 24 & 255) / 255.0F;
        float g = (float) (color >> 16 & 255) / 255.0F;
        float h = (float) (color >> 8 & 255) / 255.0F;
        float k = (float) (color & 255) / 255.0F;

        matrices.push();

        matrices.scale(0.5f, 0.5F, 0.5F);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.enableTexture();

        glyphPage.bindTexture();

        this.posY -= 6;

        for (int i = 0; i < text.length(); ++i) {
            char c0 = text.charAt(i);

            if (c0 == 167 && i + 1 < text.length()) {
                int i1 = "0123456789abcdefklmnor".indexOf(text.toLowerCase(Locale.ENGLISH).charAt(i + 1));

                if (i1 < 16) {
                    this.boldStyle = false;
                    this.strikethroughStyle = false;
                    this.underlineStyle = false;
                    this.italicStyle = false;

                    if (i1 < 0) {
                        i1 = 15;
                    }

                    if (shadow) {
                        i1 += 16;
                    }

                    int j1 = this.colorCode[i1];

                    g = (float) (j1 >> 16 & 255) / 255.0F;
                    h = (float) (j1 >> 8 & 255) / 255.0F;
                    k = (float) (j1 & 255) / 255.0F;
                } else if (i1 == 16) {
                } else if (i1 == 17) {
                    this.boldStyle = true;
                } else if (i1 == 18) {
                    this.strikethroughStyle = true;
                } else if (i1 == 19) {
                    this.underlineStyle = true;
                } else if (i1 == 20) {
                    this.italicStyle = true;
                } else {
                    this.boldStyle = false;
                    this.strikethroughStyle = false;
                    this.underlineStyle = false;
                    this.italicStyle = false;
                }

                ++i;
            } else {
                glyphPage = getCurrentGlyphPage();


                glyphPage.bindTexture();
                float f = glyphPage.drawChar(matrices, c0, posX, posY, g, k, h, alpha);
                RenderSystem.texParameter(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
                doDraw(matrices, f, glyphPage);
            }

        }

        glyphPage.unbindTexture();
        matrices.pop();
    }

    private float[] parseHexColor(String hex) {
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }
        int color = Integer.parseInt(hex, 16);
        return new float[]{
                (color >> 16 & 255) / 255.0F,
                (color >> 8 & 255) / 255.0F,
                (color & 255) / 255.0F,
                1.0F // Alpha (необязательно)
        };
    }

    private void doDraw(MatrixStack matrices, float f, GlyphPage glyphPage) {
        if (this.strikethroughStyle) {
            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
            RenderSystem.disableTexture();
            bufferBuilder.begin(GlStateManager.GL_QUADS, DefaultVertexFormats.POSITION);
            bufferBuilder
                    .pos(this.posX, this.posY + (float) (glyphPage.getMaxFontHeight() / 2), 0.0D)
                    .endVertex();
            bufferBuilder.pos(matrices.getLast().getMatrix(), this.posX + f,
                    this.posY + (float) (glyphPage.getMaxFontHeight() / 2), 0.0F).endVertex();
            bufferBuilder.pos(matrices.getLast().getMatrix(), this.posX + f,
                    this.posY + (float) (glyphPage.getMaxFontHeight() / 2) - 1.0F, 0.0F).endVertex();
            bufferBuilder.pos(matrices.getLast().getMatrix(), this.posX,
                    this.posY + (float) (glyphPage.getMaxFontHeight() / 2) - 1.0F, 0.0F).endVertex();
            WorldVertexBufferUploader.draw(bufferBuilder);
            RenderSystem.enableTexture();
        }

        if (this.underlineStyle) {
            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
            RenderSystem.disableTexture();
            bufferBuilder.begin(GlStateManager.GL_QUADS, DefaultVertexFormats.POSITION);
            int l = this.underlineStyle ? -1 : 0;
            bufferBuilder.pos(matrices.getLast().getMatrix(), this.posX + (float) l,
                    this.posY + (float) glyphPage.getMaxFontHeight(), 0.0F).endVertex();
            bufferBuilder
                    .pos(matrices.getLast().getMatrix(), this.posX + f, this.posY + (float) glyphPage.getMaxFontHeight(), 0.0F)
                    .endVertex();
            bufferBuilder.pos(matrices.getLast().getMatrix(), this.posX + f,
                    this.posY + (float) glyphPage.getMaxFontHeight() - 1.0F, 0.0F).endVertex();
            bufferBuilder.pos(matrices.getLast().getMatrix(), this.posX + (float) l,
                    this.posY + (float) glyphPage.getMaxFontHeight() - 1.0F, 0.0F).endVertex();
            WorldVertexBufferUploader.draw(bufferBuilder);
            RenderSystem.enableTexture();
        }

        this.posX += f;
    }

    private GlyphPage getCurrentGlyphPage() {
        if (boldStyle && italicStyle)
            return boldItalicGlyphPage;
        else if (boldStyle)
            return boldGlyphPage;
        else if (italicStyle)
            return italicGlyphPage;
        else
            return regularGlyphPage;
    }

    /**
     * Reset all style flag fields in the class to false; called at the start of
     * string rendering
     */
    private void resetStyles() {
        this.boldStyle = false;
        this.italicStyle = false;
        this.underlineStyle = false;
        this.strikethroughStyle = false;
    }

    public int getHeight() {
        return regularGlyphPage.getMaxFontHeight() / 2;
    }
    private Map<String, Integer> widthMap = new LinkedHashMap<>();
    public int getWidth(ITextComponent textComponent) {
        return getWidth(textComponent.getString());
    }
    public int getStringWidth(ITextComponent textComponent) {
        return getWidth(textComponent.getString());
    }
    public int getStringWidth(String textComponent) {
        return getWidth(textComponent);
    }

    public int getWidth(String str) {
        if (widthMap.containsKey(str)) {
            return widthMap.get(str);
        } else {
            int width = calculateWidth(str);
            widthMap.put(str, width);
            return width;
        }
    }

    private int calculateWidth(String str) {
        //str = replaceUtfSymbolz(str);
        String text = removeColorCodes(str);
        if (text == null) {
            return 0;
        }
        int width = 0;

        GlyphPage currentPage;

        int size = text.length();

        boolean on = false;

        for (int i = 0; i < size; i++) {
            char character = text.charAt(i);

            if (character == '\u00A7') {
                on = true;
            } else if (on && character >= '0' && character <= 'r') {
                int colorIndex = "0123456789abcdefklmnor".indexOf(character);
                if (colorIndex < 16) {
                    boldStyle = false;
                    italicStyle = false;
                } else if (colorIndex == 17) {
                    boldStyle = true;
                } else if (colorIndex == 20) {
                    italicStyle = true;
                } else if (colorIndex == 21) {
                    boldStyle = false;
                    italicStyle = false;
                }
                i++;
                on = false;
            } else {
                if (on)
                    i--;

                character = text.charAt(i);

                currentPage = getCurrentGlyphPage();

                width += currentPage.getWidth(character) - 8;
            }
        }

        return width / 2;
    }

    public static String removeColorCodes(String text) {
        String str = text;
        String[] colorCodes = new String[]{
                "4", "c", "6", "e", "2", "a", "b", "3", "1", "9", "d",
                "5", "f", "7", "8", "0", "k", "m", "o", "l", "n", "r"};
        for (String c : colorCodes) {
            str = str.replace("§" + c, "");
        }
        return str.trim();
    }

    /**
     * Trims a string to fit a specified Width.
     */
    public String trimStringToWidth(String text, int width) {
        return this.trimStringToWidth(text, width, false);
    }

    /**
     * Trims a string to a specified width, and will reverse it if par3 is set.
     */
    public String trimStringToWidth(String str, int maxWidth, boolean reverse) {
        StringBuilder stringbuilder = new StringBuilder();
        boolean on = false;

        String text = removeColorCodes(str);
        int j = reverse ? text.length() - 1 : 0;
        int k = reverse ? -1 : 1;
        int width = 0;

        GlyphPage currentPage;

        for (int i = j; i >= 0 && i < text.length() && i < maxWidth; i += k) {
            char character = text.charAt(i);

            if (character == '\u00A7') {
                on = true;
            } else if (on && character >= '0' && character <= 'r') {
                int colorIndex = "0123456789abcdefklmnor".indexOf(character);
                if (colorIndex < 16) {
                    boldStyle = false;
                    italicStyle = false;
                } else if (colorIndex == 17) {
                    boldStyle = true;
                } else if (colorIndex == 20) {
                    italicStyle = true;
                } else if (colorIndex == 21) {
                    boldStyle = false;
                    italicStyle = false;
                }
                i++;
                on = false;
            } else {
                if (on)
                    i--;

                character = text.charAt(i);

                currentPage = getCurrentGlyphPage();

                width += (currentPage.getWidth(character) - 8) / 2;
            }

            if (i > width) {
                break;
            }

            if (reverse) {
                stringbuilder.insert(0, character);
            } else {
                stringbuilder.append(character);
            }
        }

        return stringbuilder.toString();
    }

    public static String replaceMinecrftSymbols(String start) {
        return start.replace("", "");
    }

    public static ICharComponent[] loadCharArray(ITextComponent component) {
        ICharComponent[] charComponents = new ICharComponent[0];
        int i = 0;
        for (ITextComponent s : component.getSiblings()) {
            charComponents = new ICharComponent[s.getSiblings().size()];
            for (ITextComponent s1 : s.getSiblings()) {
                charComponents[i] = new ICharComponent(replaceMinecrftSymbols(s1.getString()),
                        new Color(s1.getStyle().getColor() != null ? s1.getStyle().getColor().getColor() : -1));
                i++;
            }
        }
        return charComponents;
    }

    public int draw(ICharComponent[] renderProcessor, double x, double y) {
        MatrixStack matrices = new MatrixStack();
        int width = 0;
        for (ICharComponent iCharComponent : renderProcessor) {
            width += draw(matrices, iCharComponent.symbol, (float) x + width, (float) y, iCharComponent.color.getRGB(), false);
        }
        return width;
    }

    public int getWidth(ICharComponent[] renderProcessor) {
        int width = 0;
        for (ICharComponent iCharComponent : renderProcessor) {
            width += getWidth(iCharComponent.symbol);
        }
        return width;
    }

    @AllArgsConstructor
    public static class ICharComponent {
        public String symbol;
        public Color color;
    }
}