package net.seliba.thirdpersonspectator.utils;

import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

/**
 * Utility class offering methods for working with the {@link Material} class.
 */
public final class MaterialUtils {

    /*
    Hardcoded because of various reasons, mostly because we don't have access to the ConfigurationProvider here.
     */
    private static final List<Material> transparentMaterials = Arrays.asList(
            Material.AIR,
            Material.CAVE_AIR,
            Material.VOID_AIR,
            Material.TALL_GRASS,
            Material.SUNFLOWER,
            Material.PEONY,
            Material.LILAC,
            Material.ROSE_BUSH,
            Material.WATER,
            Material.TALL_SEAGRASS,
            Material.SEAGRASS,
            Material.GRASS,
            Material.FERN,
            Material.LARGE_FERN,
            Material.KELP_PLANT,
            Material.SUGAR_CANE
    );

    /**
     * Returns whether a Player can walk through the provided Material.
     * Used instead of {@link Material#isTransparent()} because of deprecation.
     *
     * @param material The Material which should be checked.
     * @return Whether or not this Material is transparent.
     */
    public static boolean isTransparent(Material material) {
        return transparentMaterials.contains(material);
    }

}
