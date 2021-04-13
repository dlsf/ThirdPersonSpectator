package net.seliba.thirdpersonspectator.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;

/**
 * Utility class for constructing {@link ItemStack}s.
 * Methods are intended for chaining.
 */
public final class ItemBuilder {

    private final ItemStack itemStack;
    private ItemMeta itemMeta;

    /**
     * The default constructor.
     * Initializes this ItemBuilder with the provided item type.
     *
     * @param type The Material of this item.
     */
    public ItemBuilder(Material type) {
        this.itemStack = new ItemStack(type);
        this.itemMeta = itemStack.getItemMeta();
    }

    /**
     * Changes the name of this item.
     *
     * @param name The new name for this item.
     * @return The instance of this ItemBuilder.
     */
    public ItemBuilder name(String name) {
        itemMeta.setDisplayName(name);
        return this;
    }

    /**
     * Changes the lore of this item.
     *
     * @param lore The new lore for this item.
     * @return The instance of this ItemBuilder.
     */
    public ItemBuilder lore(String... lore) {
        itemMeta.setLore(Arrays.asList(lore));
        return this;
    }

    /**
     * Changes the skull owner of this item.
     * May only be used for player heads, will throw a {@link ClassCastException} otherwise.
     *
     * @param owner The new owner of this skull.
     * @return The instance of this ItemBuilder.
     */
    public ItemBuilder skullOwner(Player owner) {
        SkullMeta skullMeta = (SkullMeta) itemMeta;
        skullMeta.setOwningPlayer(owner);
        itemMeta = skullMeta;
        return this;
    }

    /**
     * Builds and returns a new ItemStack based on the builder configuration.
     *
     * @return The new ItemStack.
     */
    public ItemStack build() {
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

}
