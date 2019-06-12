package tech.meyerzinn.autoquarry.util;

import com.google.common.collect.ImmutableMap;
import org.bukkit.Material;

public class Fuel {
    public static final ImmutableMap<Material, Integer> sources = ImmutableMap.<Material, Integer>builder()
            .put(Material.LAVA_BUCKET, 100 * 8)
            .put(Material.COAL_BLOCK, 80 * 8)
            .put(Material.DRIED_KELP_BLOCK, 20 * 8)
            .put(Material.BLAZE_ROD, 12 * 8)
            .put(Material.COAL, 64)
            .put(Material.CHARCOAL, 64)
            .build();
}
