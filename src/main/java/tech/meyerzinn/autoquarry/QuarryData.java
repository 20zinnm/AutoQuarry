package tech.meyerzinn.autoquarry;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dispenser;
import org.bukkit.block.data.Directional;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import tech.meyerzinn.autoquarry.event.QuarryDataUpdateEvent;
import tech.meyerzinn.autoquarry.event.QuarryUpgradeEvent;
import tech.meyerzinn.autoquarry.upgrade.Fortune;
import tech.meyerzinn.autoquarry.upgrade.FuelCapacity;
import tech.meyerzinn.autoquarry.upgrade.MiningRadius;
import tech.meyerzinn.autoquarry.upgrade.MiningSpeed;
import tech.meyerzinn.autoquarry.util.BlockLocation;
import tech.meyerzinn.autoquarry.util.QuarryDataType;
import tech.meyerzinn.autoquarry.util.RomanNumerals;

import java.util.*;

public class QuarryData {

    private static final Map<Fortune, ItemStack> fortuneTools = new HashMap<>();
    private static final NamespacedKey key = new NamespacedKey(JavaPlugin.getPlugin(AutoQuarryPlugin.class), "quarry-data");

    static {
        for (Fortune f : Fortune.values()) {
            ItemStack is = new ItemStack(Material.DIAMOND_PICKAXE);
            if (f.ordinal() > 0) {
                is.addEnchantment(Enchantment.LOOT_BONUS_BLOCKS, f.ordinal());
            }
            fortuneTools.put(f, is);
        }
    }

    // location is used as the primary key for the quarry--since it is persisted into
    private transient BlockLocation location;
    private BlockLocation target; // if null, it means this quarry should restart mining
    private int fuel = 0;
    private Fortune fortune = Fortune.NONE;
    private MiningSpeed speed = MiningSpeed.NONE;
    private MiningRadius size = MiningRadius.FOUR;
    private FuelCapacity capacity = FuelCapacity.NONE;
    private Map<Material, Boolean> filters = new HashMap<>(); // stores overrides specific to this quarry
    private int blocksScanned = 0;
    private int blocksMined = 0;

    public ItemStack getTool() {
        return fortuneTools.get(fortune);
    }

    public int getBlocksTotal() {
        return (location.y - 2) * (size.radius * 2 + 1) * (size.radius * 2 + 1);
    }

    public static Optional<QuarryData> fromBlockLocation(BlockLocation l) {
        if (l.getBlock().getType() == Material.DISPENSER) {
            Dispenser disp = (Dispenser) l.getBlock().getState();
            if (disp.getPersistentDataContainer().has(key, new QuarryDataType())) {
                QuarryData qd = disp.getPersistentDataContainer().get(key, new QuarryDataType());
                Objects.requireNonNull(qd);
                qd.location = l;
                return Optional.of(qd);
            }
        }
        return Optional.empty();
    }

    public static Optional<QuarryData> fromItemStack(ItemStack is, BlockLocation l) {
        if (is.getType() == Material.DISPENSER) {
            if (is.getItemMeta() == null) return Optional.empty();
            if (is.getItemMeta().getPersistentDataContainer().has(key, new QuarryDataType())) {
                QuarryData qd = is.getItemMeta().getPersistentDataContainer().get(key, new QuarryDataType());
                Objects.requireNonNull(qd);
                qd.location = l;
                return Optional.of(qd);
            }
        }
        return Optional.empty();
    }

    public ItemStack toItemStack() {
        ItemStack is = new ItemStack(Material.DISPENSER);
        ItemMeta im = Objects.requireNonNull(is.getItemMeta());
        im.setDisplayName("Quarry");
        ArrayList<String> lore = new ArrayList<>(Arrays.asList(String.format("Fuel: %s/%s", fuel, capacity.capacity), "Upgrades: "));
        if (size.ordinal() > 0) {
            lore.add(String.format("- Mining Radius %s", RomanNumerals.convert(size.ordinal())));
        }
        if (speed.ordinal() > 0) {
            lore.add(String.format("- Mining Speed %s", RomanNumerals.convert(speed.ordinal())));
        }
        if (capacity.ordinal() > 0) {
            lore.add(String.format("- Fuel Capacity %s", RomanNumerals.convert(capacity.ordinal())));
        }
        if (fortune.ordinal() > 0) {
            lore.add(String.format("- Fortune %s", RomanNumerals.convert(fortune.ordinal())));
        }
        im.setLore(lore);
        im.getPersistentDataContainer().set(key, new QuarryDataType(), this);
        is.setItemMeta(im);
        return is;
    }

    public void placeAsBlock(BlockLocation loc) {
        this.location = loc;
        this.target = null;
        this.blocksScanned = 0;
        this.blocksMined = 0;

        Dispenser disp = (Dispenser) location.getBlock().getState();
        Directional im = (Directional) disp.getBlockData();
        im.setFacing(BlockFace.SOUTH);
        disp.setBlockData(im);
        this.updateBlock();
    }

    public void updateBlock() {
        Dispenser disp = (Dispenser) location.getBlock().getState();
        disp.getPersistentDataContainer().set(key, new QuarryDataType(), this);
        disp.update();
    }

    public BlockLocation getTarget() {
        return target;
    }

    public void nextTarget() {
        if (target.z > location.z + size.radius) {
            QuarryRunner.stop(location);
        } else if (target.x > location.x + size.radius) {
            target = new BlockLocation(target.world, location.x - size.radius, target.y, target.z + 1);
        } else if (target.y <= 1) {
            target = new BlockLocation(target.world, target.x + 1, location.y - 2, target.z);
        } else {
            target = new BlockLocation(target.world, target.x, target.y - 1, target.z);
        }
    }

    public int getFuel() {
        return fuel;
    }

    protected void doUpdate(boolean restart) {
        updateBlock();
        JavaPlugin.getPlugin(AutoQuarryPlugin.class).getServer().getPluginManager().callEvent(new QuarryDataUpdateEvent(this.location, restart));
    }

    private void doUpgrade(boolean restart) {
        updateBlock();
        JavaPlugin.getPlugin(AutoQuarryPlugin.class).getServer().getPluginManager().callEvent(new QuarryUpgradeEvent(this.location, restart));
    }

    public void setFuel(int fuel) {
        this.fuel = fuel;
        doUpdate(false);
    }

    public Fortune getFortune() {
        return fortune;
    }

    public void setFortune(Fortune fortune) {
        this.fortune = fortune;
        doUpgrade(false);
    }

    public MiningRadius getSize() {
        return size;
    }

    public void setSize(MiningRadius size) {
        this.size = size;
        this.target = null;
        doUpgrade(true);
    }

    public MiningSpeed getSeed() {
        return speed;
    }

    public void setSpeed(MiningSpeed speed) {
        this.speed = speed;
        if (QuarryRunner.isRunning(location)) {
            QuarryRunner.start(location);
        }
        doUpgrade(true);
    }

    public FuelCapacity getFuelCapacity() {
        return capacity;
    }

    public void setFuelCapacity(FuelCapacity capacity) {
        this.capacity = capacity;
        doUpgrade(false);
    }

    public void toggleFilter(Material mat) {
        if (filters.containsKey(mat)) {
            filters.put(mat, !filters.get(mat));
        } else {
            filters.put(mat, !AutoQuarryPlugin.defaultFilters.get(mat));
        }
        doUpdate(false);
    }

    public boolean shouldMine(Material mat) {
        return filters.containsKey(mat) ? filters.get(mat) : AutoQuarryPlugin.defaultFilters.getOrDefault(mat, false);
    }

    public void step() {
        if (fuel <= 0) {
            QuarryRunner.stop(location);
            return;
        }
        if (target == null) {
            target = new BlockLocation(location.world, location.x - size.radius, location.y - 2, location.z - size.radius);
        }
        Block block = target.getBlock();
        blocksScanned++;
        if (shouldMine(block.getType())) {
            Collection<ItemStack> drops = block.getDrops(getTool());
            block.setType(Material.AIR);
            target.world.playSound(block.getLocation(), Sound.BLOCK_STONE_BREAK, 1.0f, 1.0f);
            blocksMined++;
            Collection<ItemStack> overflow = drops;
            if (location.getBelow().getBlock().getState() instanceof BlockInventoryHolder) {
                BlockInventoryHolder holder = (BlockInventoryHolder) location.getBelow().getBlock().getState();
                overflow = holder.getInventory().addItem(drops.toArray(new ItemStack[0])).values();
            }
            for (ItemStack is : overflow) {
                location.world.dropItemNaturally(location.getBelow().toLocation(), is);
            }
            fuel--;
        }
        nextTarget();
        doUpdate(false);
    }

    public MiningSpeed getSpeed() {
        return speed;
    }

    public int getBlocksScanned() {
        return blocksScanned;
    }

    public int getBlocksMined() {
        return blocksMined;
    }
}
