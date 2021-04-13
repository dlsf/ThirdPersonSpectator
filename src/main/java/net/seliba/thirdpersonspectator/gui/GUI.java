package net.seliba.thirdpersonspectator.gui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a clickable GUI.
 */
public interface GUI extends InventoryHolder {

    /**
     * Returns a new Inventory with the content of this GUI.
     *
     * @return New Inventory with content.
     */
    @Override
    @NotNull
    Inventory getInventory();

    /**
     * Called when a Player clicks in the Inventory.
     * The provided InventoryClickEvent is automatically cancelled.
     *
     * @param event The InventoryClickEvent provided by Bukkit.
     */
    void handleInventoryClick(InventoryClickEvent event);

}
