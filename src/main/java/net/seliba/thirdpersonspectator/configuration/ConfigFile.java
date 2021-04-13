package net.seliba.thirdpersonspectator.configuration;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

/**
 * Represents a yaml configuration file and offers access to it.
 */
public class ConfigFile extends YamlConfiguration {

    private final String name;
    private final JavaPlugin javaPlugin;

    private File file;

    /**
     * The default constructor.
     * Parses the config file or creates it if necessary.
     *
     * @param name       The name of the file including the file type.
     * @param javaPlugin The plugin which owns this configuration.
     */
    public ConfigFile(String name, JavaPlugin javaPlugin) {
        this.name = name;
        this.javaPlugin = javaPlugin;

        reload();
    }

    /**
     * Reloads all values from this configuration and creates the file if necessary.
     */
    private void reload() {
        file = new File(javaPlugin.getDataFolder(), name);

        try {
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    throw new RuntimeException("Could not create " + name);
                }
            }

            load(file);
        } catch (IOException exception) {
            // Do nothing
        } catch (InvalidConfigurationException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Saves this configuration to the file.
     * Should be called after using {@link ConfigFile#set(String, Object)} or {@link ConfigFile#setDefault(String, Object)}.
     */
    public void save() {
        try {
            save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the value under the specified path to the provided value if it doesn't already exist.
     *
     * @param path  The path where the value may be stored.
     * @param value The default value which should be set if the path doesn't contain any value.
     */
    public void setDefault(String path, Object value) {
        if (!isSet(path)) {
            set(path, value);
        }
    }

}