package net.optifine.http;

public interface HttpListener
{
// leaked by itskekoff; discord.gg/sk3d e9QCwfgY
    void finished(HttpRequest var1, HttpResponse var2);

    void failed(HttpRequest var1, Exception var2);
}
