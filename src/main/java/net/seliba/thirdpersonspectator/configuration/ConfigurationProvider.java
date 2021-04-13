package net.seliba.thirdpersonspectator.configuration;

import net.seliba.thirdpersonspectator.configuration.types.IConfiguration;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Provides access to configuration entries after registering the configs.
 *
 * @see IConfiguration
 */
public final class ConfigurationProvider {

    private final Map<Class<? extends IConfiguration>, ConfigFile> configurationFiles = new HashMap<>();

    /**
     * Registers a configuration and the associated file containing its data.
     *
     * @param iConfiguration An enum / class of config entries.
     * @param config         The ConfigFile which contains the configuration.
     */
    public void register(Class<? extends IConfiguration> iConfiguration, ConfigFile config) {
        configurationFiles.put(iConfiguration, config);
    }

    /**
     * Saves the config entries to the config file if necessary.
     * {@link ConfigurationProvider#register(Class, ConfigFile)} should be called first.
     *
     * @param configurationEntries The configuration entries which should be saved.
     */
    public void saveDefaultConfig(IConfiguration[] configurationEntries) {
        assert configurationEntries.length > 0;
        var configFile = this.configurationFiles.get(configurationEntries[0].getClass());

        for (IConfiguration configurationEntry : configurationEntries) {
            configFile.setDefault(configurationEntry.getConfigKey(), configurationEntry.getDefaultValue());
        }

        configFile.save();
    }

    /**
     * Returns the colored String values of the provided config entries.
     * Is able to join multiple config entries as long as they are from the same configuration sub-class.
     *
     * @param configEntries The config entries which should be accessed. Should be of the same type.
     * @return The colored and joined values of the config entries.
     */
    public String getString(IConfiguration... configEntries) {
        assert configEntries.length > 0;
        var configFile = configurationFiles.get(configEntries[0].getClass());

        return Arrays.stream(configEntries)
                .map(IConfiguration::getConfigKey)
                .map(configFile::getString)
                .map(this::colorString)
                .collect(Collectors.joining());
    }

    /**
     * Colors a String using the Bukkit color system.
     *
     * @param originalString The String which should be colored.
     * @return The String with applied colors.
     */
    private String colorString(String originalString) {
        return ChatColor.translateAlternateColorCodes('&', originalString);
    }

    /**
     * Returns the Long value of the provided config entry.
     *
     * @param configEntry The config entry which should be accessed.
     * @return The value of the config entry.
     */
    public long getLong(IConfiguration configEntry) {
        var configFile = configurationFiles.get(configEntry.getClass());
        return configFile.getLong(configEntry.getConfigKey());
    }

    /**
     * Returns a list of the colored values of the provided config entry.
     *
     * @param configEntry The config entry which should be accessed.
     * @return The value of the config entry.
     */
    public List<String> getStringList(IConfiguration configEntry) {
        var configFile = configurationFiles.get(configEntry.getClass());

        return configFile.getStringList(configEntry.getConfigKey()).stream()
                .map(this::colorString)
                .collect(Collectors.toList());
    }

}
