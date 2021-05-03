package net.seliba.thirdpersonspectator.gui;

import net.seliba.thirdpersonspectator.configuration.ConfigurationProvider;
import net.seliba.thirdpersonspectator.configuration.types.MainConfig;
import net.seliba.thirdpersonspectator.model.SpectatablePlayer;
import net.seliba.thirdpersonspectator.model.SpectatablePlayerFactory;
import net.seliba.thirdpersonspectator.utils.ItemBuilder;
import net.seliba.thirdpersonspectator.utils.SoundUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Paginated GUI which shows all {@link SpectatablePlayer}s which can be spectated.
 */
public final class SpectateGUI implements GUI {

    private final ConfigurationProvider configurationProvider;
    private int currentPage = 1;

    /**
     * The default constructor.
     *
     * @param configurationProvider The provider of configurations for the plugin.
     */
    public SpectateGUI(ConfigurationProvider configurationProvider) {
        this.configurationProvider = configurationProvider;
    }

    /**
     * Returns a new Inventory with the content of this GUI.
     *
     * @return New Inventory with content.
     */
    @NotNull
    @Override
    public Inventory getInventory() {
        // Initialize the Inventory
        long inventorySize = configurationProvider.getLong(MainConfig.INVENTORY_SIZE);
        long entriesPerPage = (int) (inventorySize - 9);
        Inventory inventory = Bukkit.createInventory(
                this,
                (int) inventorySize,
                configurationProvider.getString(MainConfig.INVENTORY_NAME)
        );

        // Find all Players that should be on this page and put representative ItemStacks in the Inventory
        SpectatablePlayerFactory.getCachedPlayers().stream()
                .filter(SpectatablePlayer::isSpectatable)
                .skip((currentPage - 1) * entriesPerPage)
                .limit(entriesPerPage)
                .map(SpectatablePlayer::getBukkitPlayer)
                .map(this::getPlayerItem)
                .forEach(inventory::addItem);

        // Add pagination items to the Inventory
        ItemStack previousPageItem = new ItemBuilder(Material.ARROW)
                .name(configurationProvider.getString(MainConfig.PREVIOUS_PAGE_BUTTON))
                .build();
        ItemStack nextPageItem = new ItemBuilder(Material.ARROW)
                .name(configurationProvider.getString(MainConfig.NEXT_PAGE_BUTTON))
                .build();
        inventory.setItem((int) entriesPerPage, previousPageItem);
        inventory.setItem((int) (inventorySize - 1), nextPageItem);

        return inventory;
    }

    /**
     * Returns the ItemStack representation of the provided Player.
     * The name may only contain {@link Player#getName()} and color codes.
     *
     * @param player The Player whose ItemStack should be returned.
     * @return A new ItemStack representing this Player.
     */
    private ItemStack getPlayerItem(Player player) {
        return new ItemBuilder(Material.PLAYER_HEAD)
                .name("§6" + player.getName())
                .lore(configurationProvider.getStringList(MainConfig.INVENTORY_LORE).toArray(String[]::new))
                .skullOwner(player)
                .build();
    }

    /**
     * Called when a Player clicks in the Inventory.
     * The provided InventoryClickEvent is automatically cancelled.
     *
     * @param event The InventoryClickEvent provided by Bukkit.
     */
    @Override
    public void handleInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        // Check for items we care about
        if (clickedItem.getType() == Material.PLAYER_HEAD) {
            handlePlayerClick(player, clickedItem);
        } else if (clickedItem.getType() == Material.ARROW) {
            handleButtonClick(player, event.getSlot());
        }
    }

    /**
     * Handles a click on a Player skull.
     * Spectates the clicked Player if possible.
     *
     * @param player      The Player who clicked on the item.
     * @param clickedItem The ItemStack which was clicked.
     */
    private void handlePlayerClick(Player player, ItemStack clickedItem) {
        player.closeInventory();

        // Execute the spectate command for the clicked Player
        String targetPlayerName = getPlayerNameByItem(clickedItem);
        player.performCommand("spectate " + targetPlayerName);
    }

    /**
     * Handles a click on a pagination button.
     * Opens the next or previous site of this GUI if available.
     *
     * @param player      The Player requesting the other page
     * @param clickedSlot The slot which was clicked by the Player.
     */
    private void handleButtonClick(Player player, int clickedSlot) {
        long inventorySize = configurationProvider.getLong(MainConfig.INVENTORY_SIZE);
        long entriesPerPage = (int) (inventorySize - 9);
        long spectatablePlayerAmount = SpectatablePlayerFactory.getCachedPlayers().stream().filter(SpectatablePlayer::isSpectatable).count();
        int maxPages = (int) Math.ceil(spectatablePlayerAmount / (double) entriesPerPage);

        // Open the requested page if available
        if (clickedSlot == entriesPerPage && currentPage != 1) {
            this.currentPage--;
            player.openInventory(this.getInventory());
        } else if (clickedSlot == inventorySize - 1 && maxPages != currentPage) {
            this.currentPage++;
            player.openInventory(this.getInventory());
        } else {
            // Requested page not available
            SoundUtils.playFailSound(player);
        }
    }

    /**
     * Strips of all color codes from an item name and returns its name.
     * Called by {@link SpectateGUI#getPlayerItem(Player)} by default.
     *
     * @param itemStack The ItemStack which has a Player name as the display name.
     * @return The name of this item without color codes.
     */
    private String getPlayerNameByItem(ItemStack itemStack) {
        String itemName = itemStack.getItemMeta().getDisplayName();
        return ChatColor.stripColor(itemName);
    }

}
