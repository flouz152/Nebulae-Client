package net.minecraft.network;

public final class ThreadQuickExitException extends RuntimeException
{
// leaked by itskekoff; discord.gg/sk3d EU45OzJX
    public static final ThreadQuickExitException INSTANCE = new ThreadQuickExitException();

    private ThreadQuickExitException()
    {
        this.setStackTrace(new StackTraceElement[0]);
    }

    public synchronized Throwable fillInStackTrace()
    {
        this.setStackTrace(new StackTraceElement[0]);
        return this;
    }
}
