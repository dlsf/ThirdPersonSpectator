package net.seliba.thirdpersonspectator.utils;

import com.github.johnnyjayjay.compatre.NmsDependent;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * Class which contains various utils for working with NMS (the net.minecraft.server package).
 */
@NmsDependent
public class NMSUtils {

    /**
     * Spawns a virtual ArmorStand at the provided Location.
     *
     * @param location The Location where the ArmorStand should be spawned at.
     * @return The newly spawned ArmorStand.
     */
    public static EntityArmorStand spawnArmorStand(Location location) {
        WorldServer world = ((CraftWorld) location.getWorld()).getHandle();
        EntityArmorStand armorStand = new EntityArmorStand(world, location.getX(), location.getY(), location.getZ());

        armorStand.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

        return armorStand;
    }

    /**
     * Shows the provided virtual ArmorStand to the provided Player.
     * {@link NMSUtils#spawnArmorStand(Location)} may be called first.
     *
     * @param player     The Player which should see the ArmorStand.
     * @param armorStand The ArmorStand which should be shown.
     */
    public static void showArmorStand(Player player, EntityArmorStand armorStand) {
        // Update the properties of the ArmorStand
        armorStand.setInvisible(true);
        armorStand.setInvulnerable(true);
        armorStand.setNoGravity(true);
        armorStand.setBasePlate(false);

        // Send the packet
        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(armorStand);
        sendPacket(player, packet);

        teleportArmorStand(Set.of(player), armorStand, armorStand.getBukkitEntity().getLocation());
    }

    /**
     * Removes the provided ArmorStand for the Player.
     *
     * @param player     The Player which should no longer see the ArmorStand.
     * @param armorStand The ArmorStand which should be removed.
     */
    public static void removeArmorStand(Player player, EntityArmorStand armorStand) {
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(armorStand.getId());
        sendPacket(player, packet);
    }

    /**
     * Teleports the provided ArmorStand to the Location and shows the change to the Players.
     * {@link NMSUtils#showArmorStand(Player, EntityArmorStand)} may be called first.
     *
     * @param players    The Players which should see the teleportation.
     * @param armorStand The ArmorStand which should be teleported.
     * @param location   The new ArmorStand of the Player.
     */
    public static void teleportArmorStand(Set<Player> players, EntityArmorStand armorStand, Location location) {
        // Set the new position of the ArmorStand, the yaw is ignored but we'll set it anyways
        armorStand.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

        // Construct the teleport packet, this does NOT include the head rotation
        PacketPlayOutEntityTeleport packetPlayOutEntityTeleport = new PacketPlayOutEntityTeleport(armorStand);

        // Construct the head rotation packet
        // Convert the yaw to an angle first because NMS wants to be special
        byte adjustedYaw = (byte) ((int) location.getYaw() * 256f / 360f);
        PacketPlayOutEntityHeadRotation packetPlayOutEntityHeadRotation = new PacketPlayOutEntityHeadRotation(armorStand, adjustedYaw);

        for (Player player : players) {
            sendPacket(player, packetPlayOutEntityTeleport);
            sendPacket(player, packetPlayOutEntityHeadRotation);
        }
    }

    /**
     * Forces the Player to spectate the provided ArmorStand.
     * Used to lock the viewing angle of Players.
     * null as the armorStand argument will force the Player to leave the ArmorStand view.
     *
     * @param player     The Player which should spectate the ArmorStand.
     * @param armorStand The ArmorStand which should be spectated.
     */
    public static void spectate(Player player, EntityArmorStand armorStand) {
        // Setting the spectator target to the Player will reset the camera
        Entity spectatorTarget = armorStand == null ? ((CraftPlayer) player).getHandle() : armorStand;
        PacketPlayOutCamera packet = new PacketPlayOutCamera(spectatorTarget);

        sendPacket(player, packet);
    }

    /**
     * Sends the provided Packet to the provided Player through the Minecraft packet system.
     *
     * @param player The receiver of the packet.
     * @param packet The Packet that should be sent.
     */
    private static void sendPacket(Player player, Packet<?> packet) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

}
