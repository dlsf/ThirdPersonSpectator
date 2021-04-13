package net.seliba.thirdpersonspectator.model;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Factory which returns {@link SpectatablePlayer} from Bukkit {@link Player}s.
 * Uses a cache to avoid redundant object creation and exposes it.
 */
public final class SpectatablePlayerFactory {

    private static final Set<SpectatablePlayer> cachedPlayers = new HashSet<>();

    /**
     * Static factory method for getting a {@link SpectatablePlayer} from a {@link Player}.
     * May return a cached {@link SpectatablePlayer} if this method was called before.
     *
     * @param bukkitPlayer The Player whose {@link SpectatablePlayer} representation should be accessed.
     * @return The {@link SpectatablePlayer} representation of the provided Player.
     */
    public static SpectatablePlayer get(Player bukkitPlayer) {
        Optional<SpectatablePlayer> cachedPlayer = findInCache(bukkitPlayer);

        if (cachedPlayer.isPresent()) {
            return cachedPlayer.get();
        }

        var player = new SpectatablePlayer(bukkitPlayer);
        cachedPlayers.add(player);

        return player;
    }

    /**
     * Tries to locate a {@link SpectatablePlayer} in the cache.
     * Empty if it doesn't contain the requested Player.
     *
     * @param bukkitPlayer The Player which should be located.
     * @return The cached Player, empty if there is none.
     */
    private static Optional<SpectatablePlayer> findInCache(Player bukkitPlayer) {
        return cachedPlayers.stream()
                .filter(spectatablePlayer -> spectatablePlayer.getBukkitPlayer() == bukkitPlayer)
                .findFirst();
    }

    /**
     * Returns all cached spectatable Players.
     * Players will be cached in the {@link net.seliba.thirdpersonspectator.listener.PlayerJoinListener}
     * automatically so this should always return the representation of all online Players.
     *
     * @return All {@link SpectatablePlayer}s that are currently cached.
     */
    public static Set<SpectatablePlayer> getCachedPlayers() {
        return cachedPlayers;
    }

}
