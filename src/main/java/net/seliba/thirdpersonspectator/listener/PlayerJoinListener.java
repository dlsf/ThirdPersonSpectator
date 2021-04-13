package net.seliba.thirdpersonspectator.listener;

import net.seliba.thirdpersonspectator.model.SpectatablePlayerFactory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Event Listener which handles joining Players.
 * Registers them in the {@link net.seliba.thirdpersonspectator.model.SpectatablePlayerFactory} cache.
 */
public final class PlayerJoinListener implements Listener {

    /**
     * Called when a Player joins the server.
     * Registers them in the {@link net.seliba.thirdpersonspectator.model.SpectatablePlayerFactory} cache.
     *
     * @param event The PlayerJoinEvent provided by Bukkit.
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Add the Player to the spectatable player cache
        // Required for the SpectateGUI to work
        SpectatablePlayerFactory.get(event.getPlayer());
    }

}
