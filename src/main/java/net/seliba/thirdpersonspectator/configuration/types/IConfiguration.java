package net.seliba.thirdpersonspectator.configuration.types;

/**
 * Represents a set of configuration entries.
 * Intended to be implemented by enums.
 * Inheriting enums should be registered in the {@link net.seliba.thirdpersonspectator.configuration.ConfigurationProvider}.
 */
public interface IConfiguration {

    /**
     * The key of this config entry used in the config file.
     *
     * @return The key of this config entry.
     */
    String getConfigKey();

    /**
     * The default value of this config option.
     * May be supported by the serialization library.
     *
     * @return The default value of this config entry.
     */
    Object getDefaultValue();

}
