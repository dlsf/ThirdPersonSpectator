package net.seliba.thirdpersonspectator.scheduler;

import net.seliba.thirdpersonspectator.model.SpectatablePlayer;
import net.seliba.thirdpersonspectator.model.SpectatablePlayerFactory;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Scheduler which updates the ArmorStand positions for the Player spectating system periodically.
 */
public final class SpectatorScheduler {

    private boolean hasStarted = false;

    /**
     * Starts this scheduler.
     * May only be called once.
     *
     * @param plugin The plugin which owns the task.
     */
    public void start(Plugin plugin) {
        if (!hasStarted) {
            Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin,
                    () -> SpectatablePlayerFactory.getCachedPlayers().forEach(SpectatablePlayer::updateCamera),
                    1L,
                    1L
            );

            hasStarted = true;
        }
    }

}
