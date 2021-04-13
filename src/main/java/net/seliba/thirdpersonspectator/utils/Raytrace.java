package net.seliba.thirdpersonspectator.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Class which represents a Raytrace.
 * Contains various utils for working with Vectors and Locations.
 */
public final class Raytrace {

    private final Vector origin;
    private final Vector direction;

    /**
     * The default constructor.
     * Initializes a new Raytrace with the provided origin in the provided direction.
     * The origin should be the location vector of a {@link Location}.
     *
     * @param origin    The Vector where this Raytrace should start.
     * @param direction The direction of this Raytrace.
     */
    public Raytrace(Vector origin, Vector direction) {
        this.origin = origin;
        this.direction = direction;
    }

    /**
     * Returns all location Vectors this Raytrace contains when traversing into the direction for the provided distance.
     *
     * @param distance The distance which should be traversed.
     * @param accuracy The distance from which a new vector should be added to the list.
     * @return All Vectors which this Raytrace traverses.
     */
    public Set<Vector> traverse(double distance, double accuracy) {
        Set<Vector> locationVectors = new LinkedHashSet<>();

        // Add all blocks this Raytrace traverses to the List
        for (double d = 0; d <= distance; d += accuracy) {
            locationVectors.add(getVectorAtPostion(d));
        }

        return locationVectors;
    }

    /**
     * Traverses this Raytrace into the direction for the provided distance and returns the resulting Vector.
     *
     * @param distance The distance which should be traversed.
     * @return The resulting Vector.
     */
    private Vector getVectorAtPostion(double distance) {
        return origin.clone().add(direction.clone().multiply(distance));
    }

    /**
     * Returns the Location that is the farthest away from the origin of this Raytrace while traversing for the provided distance without a block in the way.
     *
     * @param distance The distance which should be traversed.
     * @param accuracy The distance between location checks.
     * @param world    The world the blocks should be checked in.
     * @return The farthest location from the origin without blocks in the way.
     */
    public Location getMostDistantLocation(double distance, double accuracy, World world) {
        Location mostDistantLocation = null;

        for (Vector vector : traverse(distance, accuracy)) {
            var possibleLocation = vector.toLocation(world);

            // Blocks would be in the way, we already found the most distant location
            if (!MaterialUtils.isTransparent(possibleLocation.getBlock().getType())) {
                break;
            }

            mostDistantLocation = possibleLocation;
        }

        return mostDistantLocation;
    }

    /**
     * Returns whether or not there a blocks in the way when traversing this Raytrace for the provided distance.
     *
     * @param distance The distance which should be traversed.
     * @param accuracy The distance between location checks.
     * @param world    The world the blocks should be checked in.
     * @return Whether or not there a blocks in the direction.
     */
    public boolean hasBlocksInTheWay(double distance, double accuracy, World world) {
        // Check all the blocks this Raytrace traverses
        return traverse(distance, accuracy).stream()
                .map(vector -> vector.toLocation(world))
                .map(Location::getBlock)
                .map(Block::getType)
                .noneMatch(MaterialUtils::isTransparent);
    }

}