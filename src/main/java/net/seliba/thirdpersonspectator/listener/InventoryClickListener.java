package net.seliba.thirdpersonspectator.listener;

import net.seliba.thirdpersonspectator.gui.GUI;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Event Listener which handles inventory clicks.
 * Used for the GUI system.
 *
 * @see GUI
 */
public final class InventoryClickListener implements Listener {

    /**
     * Called when a Player clicks in an Inventory.
     * Used for the GUI system.
     *
     * @param event The InventoryClickEvent provided by Bukkit.
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null || event.getCurrentItem() == null) {
            return;
        }

        var inventoryHolder = event.getClickedInventory().getHolder();

        // Let the GUI handle the click if the clicked Inventory is one and cancel the event
        if (inventoryHolder instanceof GUI) {
            event.setResult(Event.Result.DENY);
            ((GUI) inventoryHolder).handleInventoryClick(event);
        }
    }

}
