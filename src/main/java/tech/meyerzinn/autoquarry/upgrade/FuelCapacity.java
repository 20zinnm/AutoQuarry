package tech.meyerzinn.autoquarry.upgrade;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tech.meyerzinn.autoquarry.AutoQuarryPlugin;
import tech.meyerzinn.autoquarry.util.RomanNumerals;

import java.util.Collections;
import java.util.Objects;

public enum FuelCapacity {
    NONE(Material.FURNACE, 0, 5000),
    ONE(Material.BLAST_FURNACE, 5000, 10000);

    public Material icon;
    public double cost;
    public int capacity;

    FuelCapacity(Material icon, double cost, int capacity) {
        this.icon = icon;
        this.cost = cost;
        this.capacity = capacity;
    }

    public FuelCapacity next() {
        if (ordinal() == values().length - 1) return null;
        return values()[ordinal() + 1];
    }

    public ItemStack getIcon() {
        ItemStack item = new ItemStack(icon);
        ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
        meta.setDisplayName(String.format("Fuel Capacity %s (%s)", RomanNumerals.convert(ordinal()), AutoQuarryPlugin.econ.format(cost)));
        meta.setLore(Collections.singletonList("Increases fuel storage capacity."));
        return item;
    }
}
