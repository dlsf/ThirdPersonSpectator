package net.seliba.thirdpersonspectator.model;

import com.github.johnnyjayjay.compatre.NmsDependent;
import io.papermc.lib.PaperLib;
import net.minecraft.server.v1_13_R2.EntityArmorStand;
import net.seliba.thirdpersonspectator.ThirdPersonSpectator;
import net.seliba.thirdpersonspectator.utils.NMSUtils;
import net.seliba.thirdpersonspectator.utils.Raytrace;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a Player which can but doesn't have to be spectated by other Players.
 * Uses a virtual ArmorStand to force spectators into their third-person view.
 * Spectatable = can be spectated.
 */
@NmsDependent
public final class SpectatablePlayer {

    private final Player bukkitPlayer;
    private final Set<Player> spectatingPlayers = new HashSet<>();

    private EntityArmorStand armorStand;
    private Location lastPlayerLocation;

    /**
     * The default constructor.
     * Gets a {@link SpectatablePlayer} from a {@link Player}.
     *
     * @param bukkitPlayer The Bukkit player which should be wrapped.
     */
    SpectatablePlayer(Player bukkitPlayer) {
        this.bukkitPlayer = bukkitPlayer;
        this.lastPlayerLocation = bukkitPlayer.getLocation();
    }

    /**
     * Returns this Player's Bukkit implementation.
     *
     * @return The Bukkit implementation of this Player.
     */
    public Player getBukkitPlayer() {
        return bukkitPlayer;
    }

    /**
     * Returns a Set of all the spectators of this Player.
     * Can be empty if there are none.
     *
     * @return Set of all spectators.
     */
    public Set<Player> getSpectatingPlayers() {
        return spectatingPlayers;
    }

    /**
     * Returns whether or not this Player is currently able to be spectated.
     *
     * @return Whether or not this Player can be spectated.
     */
    public boolean isSpectatable() {
        return bukkitPlayer.getGameMode() != GameMode.SPECTATOR;
    }

    /**
     * Forces the provided Player to spectate this Player from a third-person view.
     * Might create a new virtual armor stand if necessary.
     *
     * @param spectator The Player who wants to spectate this Player.
     */
    public void startSpectating(Player spectator) {
        spectatingPlayers.add(spectator);
        spectator.setGameMode(GameMode.SPECTATOR);

        // Hide all the other spectators for a better viewing experience
        for (SpectatablePlayer spectatablePlayer : SpectatablePlayerFactory.getCachedPlayers()) {
            for (Player spectatingPlayer : spectatablePlayer.getSpectatingPlayers()) {
                spectatingPlayer.hidePlayer(ThirdPersonSpectator.getPlugin(ThirdPersonSpectator.class), spectator);
                spectator.hidePlayer(ThirdPersonSpectator.getPlugin(ThirdPersonSpectator.class), spectatingPlayer);
            }
        }

        // Start spectating, teleport the spectator first so the ArmorStand packet is not just ignored
        PaperLib.teleportAsync(spectator, bukkitPlayer.getLocation()).thenRun(() -> {
            // Spawn the spectator ArmorStand if necessary
            if (armorStand == null) {
                armorStand = NMSUtils.spawnArmorStand(calculateArmorStandLocation());
            }

            // Show the ArmorStand to the Player and start spectating
            NMSUtils.showArmorStand(spectator, armorStand);
            NMSUtils.spectate(spectator, armorStand);
        });
    }

    /**
     * Removes the provided Player from the forced third-person view and hides the virtual ArmorStand from him.
     *
     * @param spectator The Player which should no longer spectate.
     */
    public void stopSpectating(Player spectator) {
        spectatingPlayers.remove(spectator);

        // Force the Player out of the spectating view and hide the ArmorStand
        NMSUtils.spectate(spectator, null);
        NMSUtils.removeArmorStand(spectator, armorStand);

        // Show the spectator again
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.showPlayer(ThirdPersonSpectator.getPlugin(ThirdPersonSpectator.class), spectator);
            spectator.showPlayer(ThirdPersonSpectator.getPlugin(ThirdPersonSpectator.class), player);
        }

        // Remove the ArmorStand if there are no more spectators
        if (spectatingPlayers.isEmpty()) {
            removeArmorStand();
        }
    }

    /**
     * Hides this Players ArmorStand from all spectators.
     */
    private void removeArmorStand() {
        if (armorStand != null) {
            for (Player spectator : spectatingPlayers) {
                NMSUtils.spectate(spectator, null);
                NMSUtils.removeArmorStand(spectator, armorStand);
            }

            armorStand = null;
        }
    }

    /**
     * Updates the camera position for this Player.
     * Teleports the virtual ArmorStand for the third-person view.
     * Calling this for a Player without spectators or a Player which has not moved since the last execution will be pretty fast.
     * Intended for getting called many times, e.g. through a Scheduler or the {@link org.bukkit.event.player.PlayerMoveEvent}.
     */
    public void updateCamera() {
        // Ignore this Player if there are no spectators
        if (spectatingPlayers.isEmpty() || armorStand == null) {
            return;
        }

        // Check if this Player is no longer spectatable
        if (!isSpectatable()) {
            spectatingPlayers.forEach(this::stopSpectating);
            return;
        }

        // Return if the Player hasn't moved to save resources
        if (lastPlayerLocation.equals(bukkitPlayer.getLocation())) {
            return;
        } else {
            lastPlayerLocation = bukkitPlayer.getLocation();
        }

        var armorStandLocation = calculateArmorStandLocation();

        // Teleport the ArmorStand
        NMSUtils.teleportArmorStand(spectatingPlayers, armorStand, armorStandLocation);

        // Teleport the spectators so the spectated Player is never out of sight
        for (Player spectator : spectatingPlayers) {
            spectator.teleport(armorStandLocation);
        }
    }

    /**
     * Calculates the next position of the camera ArmorStand.
     * <p>
     * This will either be normal third-person, a birds-eye view or optionally
     * the Player's first-person view based on the environment.
     * As this method will be called very often when there are many spectators,
     * it should be reasonably fast.
     *
     * @return The next Location of the ArmorStand.
     */
    private Location calculateArmorStandLocation() {
        Location location = bukkitPlayer.getEyeLocation();
        Vector origin = location.toVector();
        Vector direction = location.getDirection();

        // Use a Raytrace to check if and from where there is a line of sight
        Raytrace raytrace = new Raytrace(origin, direction.clone().multiply(-1).normalize());
        Location mostDistantLocation = raytrace.getMostDistantLocation(4, 0.5, location.getWorld());

        // Check if we were able to find a position for the ArmorStand or if the position is too close to the Player
        if (mostDistantLocation == null) {
            mostDistantLocation = tryBirdsEyeView(location);
        } else if (location.getPitch() < -55) {
            // Teleport the spectator into the Player, no third-person view could be found
            mostDistantLocation = location.clone().subtract(0, 0.25, 0);
            mostDistantLocation.setDirection(location.getDirection());
        } else {
            // Adjust the direction to have the spectated player in focus
            mostDistantLocation.setDirection(direction);
        }

        // Adjust the ArmorStands position to match the Vanilla third-person view
        return mostDistantLocation.clone().subtract(0, 1.5, 0);
    }

    /**
     * Checks if a birds-eye view on the Player is possible, uses the Player's location for the camera if not.
     *
     * @param playerLocation The Location of the Player which should be spectated.
     * @return Either the Location of the birds-eye view or the Location of the Player.
     */
    private Location tryBirdsEyeView(Location playerLocation) {
        // Use a Raytrace to check if there is a line of sight
        Raytrace birdViewRaytrace = new Raytrace(playerLocation.toVector(), new Vector(0, 1, 0));
        Location cameraLocation;

        // Check if there is a line of sight for the birds-eye view
        if (birdViewRaytrace.hasBlocksInTheWay(4, 0.5, playerLocation.getWorld())) {
            // Teleport the spectator slightly above the Player, no third-person view could be found
            cameraLocation = playerLocation.clone().add(0, 0.25, 0);
            cameraLocation.setDirection(playerLocation.getDirection());
        } else {
            // Use the birds-eye view
            cameraLocation = playerLocation.clone().add(0, 4, 0);
            cameraLocation.setYaw(playerLocation.getYaw());
            cameraLocation.setPitch(90);
        }

        return cameraLocation;
    }

}
