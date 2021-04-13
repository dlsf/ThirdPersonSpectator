package net.seliba.thirdpersonspectator.listener;

import net.seliba.thirdpersonspectator.model.SpectatablePlayerFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Event Listener which handles quitting Players.
 * Used for moving spectators out of the third-person view and forcing all spectators out of spectated quitting Players.
 */
public final class PlayerQuitListener implements Listener {

    /**
     * Called when a Player leaves the server.
     * Used for moving spectators out of the third-person view and forcing all spectators out of spectated quitting Players.
     *
     * @param event The PlayerQuitEvent provided by Bukkit.
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player quittingPlayer = event.getPlayer();
        var spectatablePlayer = SpectatablePlayerFactory.get(quittingPlayer);

        // Remove the leaving Player from the cache
        SpectatablePlayerFactory.getCachedPlayers().removeIf(player -> player == spectatablePlayer);

        // Force all spectators of this Player to stop spectating
        SpectatablePlayerFactory.getCachedPlayers().stream()
                .filter(player -> player.getSpectatingPlayers().contains(quittingPlayer))
                .forEach(player -> player.stopSpectating(quittingPlayer));
    }

}
