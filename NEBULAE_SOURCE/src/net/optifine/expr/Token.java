package net.optifine.expr;

public class Token
{
// leaked by itskekoff; discord.gg/sk3d QHFL88zK
    private TokenType type;
    private String text;

    public Token(TokenType type, String text)
    {
        this.type = type;
        this.text = text;
    }

    public TokenType getType()
    {
        return this.type;
    }

    public String getText()
    {
        return this.text;
    }

    public String toString()
    {
        return this.text;
    }
}
