package net.minecraft.client.util;

public interface IMutableSearchTree<T> extends ISearchTree<T>
{
// leaked by itskekoff; discord.gg/sk3d IcRZLtAm
    void func_217872_a(T element);

    void clear();

    /**
     * Recalculates the contents of this search tree, reapplying {@link #nameFunc} and {@link #idFunc}. Should be called
     * whenever resources are reloaded (e.g. language changes).
     */
    void recalculate();
}
