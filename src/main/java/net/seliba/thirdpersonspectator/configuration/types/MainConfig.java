package net.seliba.thirdpersonspectator.configuration.types;

import java.util.Collections;

/**
 * The main config the plugin.
 * Contains general values that are not send in chat.
 */
public enum MainConfig implements IConfiguration {

    /**
     * The name of the {@link net.seliba.thirdpersonspectator.gui.SpectateGUI}.
     */
    INVENTORY_NAME("spectate-gui.name", "&c&lPlayers"),

    /**
     * The size of the {@link net.seliba.thirdpersonspectator.gui.SpectateGUI}.
     * Has to be dividable by 9.
     */
    INVENTORY_SIZE("spectate-gui.size", 6L * 9L),

    /**
     * The lore of the Player heads in the {@link net.seliba.thirdpersonspectator.gui.SpectateGUI}.
     */
    INVENTORY_LORE("spectate-gui.skull-lore", Collections.singletonList("&7Click to spectate this player")),

    /**
     * Name of the item which skips the previous page in a {@link net.seliba.thirdpersonspectator.gui.GUI}.
     */
    PREVIOUS_PAGE_BUTTON("gui.previous-button-name", "&bPrevious Page"),

    /**
     * Name of the item which skips the next page in a {@link net.seliba.thirdpersonspectator.gui.GUI}.
     */
    NEXT_PAGE_BUTTON("gui.next-button-name", "&bNext Page");

    private final String configKey;
    private final Object defaultValue;

    /**
     * The default constructor.
     *
     * @param configKey    The key of this config entry.
     * @param defaultValue The default value of this config entry.
     */
    MainConfig(String configKey, Object defaultValue) {
        this.configKey = configKey;
        this.defaultValue = defaultValue;
    }

    /**
     * The key of this config entry used in the config file.
     *
     * @return The key of this config entry.
     */
    @Override
    public String getConfigKey() {
        return configKey;
    }

    /**
     * The default value of this config option.
     * May be supported by the serialization library.
     *
     * @return The default value of this config entry.
     */
    @Override
    public Object getDefaultValue() {
        return defaultValue;
    }

}
