package net.seliba.thirdpersonspectator.utils;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Utility class which sends sounds to Players.
 */
public final class SoundUtils {

    /**
     * Sends a sound associated with a failed action.
     *
     * @param player The Player who should hear the sound.
     */
    public static void playFailSound(Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1f, 0f);
    }

}
