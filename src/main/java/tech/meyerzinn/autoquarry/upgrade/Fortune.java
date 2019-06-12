package tech.meyerzinn.autoquarry.upgrade;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import tech.meyerzinn.autoquarry.util.RomanNumerals;

import java.util.Collections;
import java.util.Objects;

public enum Fortune {
    NONE(0),
    ONE(10000),
    TWO(20000),
    THREE(40000);

    public double cost;

    Fortune(double cost) {
        this.cost = cost;
    }

    public Fortune next() {
        if (ordinal() == Fortune.values().length - 1) return null;
        return values()[ordinal() + 1];
    }

    public ItemStack getIcon() {
        ItemStack icon = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) icon.getItemMeta();
        Objects.requireNonNull(meta).addStoredEnchant(Enchantment.LOOT_BONUS_BLOCKS, ordinal(), false);
        meta.setDisplayName(String.format("Fortune %s (%s)", RomanNumerals.convert(ordinal()), cost));
        meta.setLore(Collections.singletonList("Substantially increases loot gained."));
        return icon;
    }
}
