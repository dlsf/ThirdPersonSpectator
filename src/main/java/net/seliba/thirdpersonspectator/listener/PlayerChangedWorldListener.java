package net.seliba.thirdpersonspectator.listener;

import net.seliba.thirdpersonspectator.model.SpectatablePlayerFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

/**
 * Event Listener which handles Player changing worlds.
 * Used to prevent spectator cameras from breaking.
 */
public class PlayerChangedWorldListener implements Listener {

    /**
     * Called when a Player changed worlds.
     * Used to prevent spectator cameras from breaking.
     *
     * @param event The PlayerChangedWorldEvent provided by Bukkit.
     */
    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        var spectatablePlayer = SpectatablePlayerFactory.get(event.getPlayer());

        for (Player spectatingPlayer : spectatablePlayer.getSpectatingPlayers()) {
            spectatablePlayer.stopSpectating(spectatingPlayer);
            spectatablePlayer.startSpectating(spectatingPlayer);
        }
    }

}
