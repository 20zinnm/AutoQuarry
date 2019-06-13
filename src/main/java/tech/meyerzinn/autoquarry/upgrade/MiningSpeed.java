package tech.meyerzinn.autoquarry.upgrade;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tech.meyerzinn.autoquarry.AutoQuarryPlugin;

import java.util.Collections;
import java.util.Objects;

public enum MiningSpeed {
    NONE(120, Material.STICK, 0),
    ONE(100, Material.WOODEN_PICKAXE, 400),
    TWO(80, Material.STONE_PICKAXE, 1600),
    THREE(60, Material.IRON_PICKAXE, 3600),
    FOUR(40, Material.GOLDEN_PICKAXE, 6400),
    FIVE(20, Material.DIAMOND_PICKAXE, 10000);

    public int ticks;
    public Material icon;
    public double cost;

    MiningSpeed(int ticks, Material icon, double cost) {
        this.ticks = ticks;
        this.icon = icon;
        this.cost = cost;
    }

    public MiningSpeed next() {
        if (ordinal() == values().length - 1) return null;
        return values()[this.ordinal() + 1];
    }

    public ItemStack getIcon() {
        ItemStack item = new ItemStack(icon);
        ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
        meta.setDisplayName(String.format("Mining Speed %s (%s)", ordinal(), AutoQuarryPlugin.econ.format(cost)));
        meta.setLore(Collections.singletonList("Mines at a rate of one block per " + ticks / 20 + " seconds."));
        item.setItemMeta(meta);
        return item;
    }
}
