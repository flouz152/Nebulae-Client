package net.minecraft.inventory;

public interface IInventoryChangedListener
{
// leaked by itskekoff; discord.gg/sk3d 10rBdKZi
    /**
     * Called by InventoryBasic.onInventoryChanged() on a array that is never filled.
     */
    void onInventoryChanged(IInventory invBasic);
}
