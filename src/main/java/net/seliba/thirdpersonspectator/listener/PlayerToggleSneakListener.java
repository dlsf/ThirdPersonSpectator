package net.seliba.thirdpersonspectator.listener;

import net.seliba.thirdpersonspectator.model.SpectatablePlayerFactory;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

/**
 * Event Listener which handles sneaking.
 * Forces Player's who are spectating out of the camera.
 */
public class PlayerToggleSneakListener implements Listener {

    /**
     * Called when a Player toggles sneaking.
     * Forces Player's who are spectating out of the camera.
     *
     * @param event The PlayerToggleSneakEvent provided by Bukkit.
     */
    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        var player = event.getPlayer();

        if (player.isSneaking()) {
            return;
        }

        if (player.getGameMode() != GameMode.SPECTATOR) {
            return;
        }

        // Stop spectating if this Player was a spectator
        SpectatablePlayerFactory.getCachedPlayers().stream()
                .filter(spectatablePlayer -> spectatablePlayer.getSpectatingPlayers().contains(player))
                .forEach(spectatablePlayer -> spectatablePlayer.stopSpectating(player));
    }

}
