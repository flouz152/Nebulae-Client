package net.optifine.util;

import java.util.HashSet;
import java.util.Set;

public class FlagEvent
{
// leaked by itskekoff; discord.gg/sk3d aSqTmHk3
    private static Set<String> setEvents = new HashSet<>();

    public static void set(String name)
    {
        synchronized (setEvents)
        {
            setEvents.add(name);
        }
    }

    public static boolean clear(String name)
    {
        synchronized (setEvents)
        {
            return setEvents.remove(name);
        }
    }

    public static boolean isActive(String name)
    {
        synchronized (setEvents)
        {
            return setEvents.contains(name);
        }
    }

    public static boolean isActiveClear(String name)
    {
        return clear(name);
    }
}
