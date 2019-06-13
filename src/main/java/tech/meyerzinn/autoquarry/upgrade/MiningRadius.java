package tech.meyerzinn.autoquarry.upgrade;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tech.meyerzinn.autoquarry.AutoQuarryPlugin;
import tech.meyerzinn.autoquarry.util.RomanNumerals;

import java.util.Collections;
import java.util.Objects;

public enum MiningRadius {
    FOUR(Material.OAK_PRESSURE_PLATE, 0, 4),
    EIGHT(Material.SPRUCE_PRESSURE_PLATE, 1600, 8),
    TWELVE(Material.BIRCH_PRESSURE_PLATE, 3600, 12),
    SIXTEEN(Material.ACACIA_PRESSURE_PLATE, 6400, 16),
    TWENTY(Material.LIGHT_WEIGHTED_PRESSURE_PLATE, 10000, 20),
    TWENTY_FOUR(Material.HEAVY_WEIGHTED_PRESSURE_PLATE, 14400, 24);

    public Material icon;
    public double cost;
    public int radius;

    MiningRadius(Material icon, double cost, int radius) {
        this.icon = icon;
        this.cost = cost;
        this.radius = radius;
    }

    public MiningRadius next() {
        if (ordinal() == values().length - 1) return null;
        return values()[ordinal() + 1];
    }

    public ItemStack getIcon() {
        ItemStack item = new ItemStack(icon);
        ItemMeta meta = item.getItemMeta();
        Objects.requireNonNull(meta).setDisplayName(String.format("Mining Radius %s (%s)", RomanNumerals.convert(ordinal()), AutoQuarryPlugin.econ.format(cost)));
        meta.setLore(Collections.singletonList("Excavates an area of " + (radius * 2 + 1) + "x" + (radius * 2 + 1) + " blocks."));
        item.setItemMeta(meta);
        return item;
    }


}
