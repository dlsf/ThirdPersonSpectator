package net.seliba.thirdpersonspectator.configuration.types;

/**
 * The messages used by this plugin.
 * Contains only values that can be send to the chat.
 */
public enum Message implements IConfiguration {

    /**
     * The prefix used in the chat.
     * Has to be applied manually.
     * @see net.seliba.thirdpersonspectator.configuration.ConfigurationProvider#getString(IConfiguration...)
     */
    PREFIX("prefix", "&9&lSpectator &r&7» "),

    /**
     * Message which is sent to the console if it tries to execute a command.
     */
    NO_PLAYER("no-player", "&cYou have to be a player to do that!"),

    /**
     * Messages which is sent when the requested Player for an operation is not online.
     */
    PLAYER_NOT_ONLINE("player-not-online", "&cPlayer not found!"),

    /**
     * Message which is sent when a Player is requesting an operation on a Player which is (currently) unavailable.
     */
    INVALID_TARGET("invalid-target", "&cYou can't spectate this player!"),

    /**
     * Message which is sent when a Player can't switch his GameMode because he is in the third-person spectator view.
     */
    GAMEMODE_CHANGE_NOT_ALLOWED("gamemode-change-not-allowed", "&cYou're not allowed to change your gamemode!");

    private final String configKey;
    private final Object defaultValue;

    /**
     * The default constructor.
     *
     * @param configKey    The key of this config entry.
     * @param defaultValue The default value of this config entry.
     */
    Message(String configKey, Object defaultValue) {
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
