package net.seliba.thirdpersonspectator.listener;

import net.seliba.thirdpersonspectator.configuration.ConfigurationProvider;
import net.seliba.thirdpersonspectator.configuration.types.Message;
import net.seliba.thirdpersonspectator.model.SpectatablePlayerFactory;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

/**
 * Event Listener which handles Players changing their game mode.
 * Used for blocking the change for third-person spectators in order to prevent a Minecraft error.
 */
public class PlayerGameModeChangeListener implements Listener {

    private final ConfigurationProvider configurationProvider;

    /**
     * The default constructor.
     *
     * @param configurationProvider The provider of configurations for the plugin.
     */
    public PlayerGameModeChangeListener(ConfigurationProvider configurationProvider) {
        this.configurationProvider = configurationProvider;
    }

    /**
     * Called when a Player changes his GameMode.
     * Used for blocking the change for third-person spectators in order to prevent a Minecraft error.
     *
     * @param event The PlayerGameModeChangeEvent provided by Bukkit.
     */
    @EventHandler
    public void onPlayerChangeGameMode(PlayerGameModeChangeEvent event) {
        var player = event.getPlayer();
        var gameMode = player.getGameMode();

        if (gameMode != GameMode.SPECTATOR) {
            return;
        }

        boolean isThirdPersonSpectator = SpectatablePlayerFactory.getCachedPlayers().stream()
                .anyMatch(spectatablePlayer -> spectatablePlayer.getSpectatingPlayers().contains(player));

        if (isThirdPersonSpectator) {
            player.sendMessage(configurationProvider.getString(Message.PREFIX, Message.GAMEMODE_CHANGE_NOT_ALLOWED));
            event.setCancelled(true);
        }
    }

}
