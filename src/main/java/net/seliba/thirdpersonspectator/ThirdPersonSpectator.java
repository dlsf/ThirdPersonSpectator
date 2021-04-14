package net.seliba.thirdpersonspectator;

import com.github.johnnyjayjay.compatre.NmsClassLoader;
import net.seliba.thirdpersonspectator.commands.SpectateCommand;
import net.seliba.thirdpersonspectator.configuration.ConfigFile;
import net.seliba.thirdpersonspectator.configuration.ConfigurationProvider;
import net.seliba.thirdpersonspectator.configuration.types.MainConfig;
import net.seliba.thirdpersonspectator.configuration.types.Message;
import net.seliba.thirdpersonspectator.listener.*;
import net.seliba.thirdpersonspectator.scheduler.SpectatorScheduler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Class which initializes the plugin.
 * {@link ThirdPersonSpectator#onEnable()} is called upon plugin startup.
 */
public final class ThirdPersonSpectator extends JavaPlugin {

    private ConfigurationProvider configurationProvider;

    /*
     * Enables multi-version support with the help of compatre (https://github.com/JohnnyJayJay/compatre).
     */
    static {
        NmsClassLoader.loadNmsDependents(ThirdPersonSpectator.class);
    }

    /**
     * The startup logic.
     */
    @Override
    public void onEnable() {
        initializeConfigs();
        registerCommand();
        registerListener();

        // Start the scheduler who updates the ArmorStand positions for the spectator cameras
        new SpectatorScheduler().start(this);

        getLogger().info("Erfolgreich gestartet!");
    }

    /**
     * Creates configuration files if necessary and registers them in the internal Config system.
     *
     * @see net.seliba.thirdpersonspectator.configuration
     */
    private void initializeConfigs() {
        this.configurationProvider = new ConfigurationProvider();

        var mainConfigFile = new ConfigFile("config.yml", this);
        var messageConfigFile = new ConfigFile("messages.yml", this);

        this.configurationProvider.register(MainConfig.class, mainConfigFile);
        this.configurationProvider.register(Message.class, messageConfigFile);

        this.configurationProvider.saveDefaultConfig(MainConfig.values());
        this.configurationProvider.saveDefaultConfig(Message.values());
    }

    /**
     * Registers all Bukkit commands and associated tab-completers.
     *
     * @see net.seliba.thirdpersonspectator.commands
     */
    private void registerCommand() {
        var spectateCommand = new SpectateCommand(configurationProvider);

        getCommand("spectate").setExecutor(spectateCommand);
        getCommand("spectate").setTabCompleter(spectateCommand);
    }

    /**
     * Registers all necessary Bukkit Listeners for this plugin.
     *
     * @see net.seliba.thirdpersonspectator.listener
     */
    private void registerListener() {
        var pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new InventoryClickListener(), this);
        pluginManager.registerEvents(new PlayerChangedWorldListener(), this);
        pluginManager.registerEvents(new PlayerGameModeChangeListener(configurationProvider), this);
        pluginManager.registerEvents(new PlayerJoinListener(), this);
        pluginManager.registerEvents(new PlayerQuitListener(), this);
        pluginManager.registerEvents(new PlayerToggleSneakListener(), this);
    }

}
