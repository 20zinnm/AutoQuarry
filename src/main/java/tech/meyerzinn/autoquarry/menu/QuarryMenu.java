package tech.meyerzinn.autoquarry.menu;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.InventoryListener;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.SmartInvsPlugin;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import tech.meyerzinn.autoquarry.AutoQuarryPlugin;
import tech.meyerzinn.autoquarry.QuarryData;
import tech.meyerzinn.autoquarry.QuarryRunner;
import tech.meyerzinn.autoquarry.util.BlockLocation;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class QuarryMenu implements InventoryProvider, Listener {

    private static final JavaPlugin plugin = JavaPlugin.getPlugin(AutoQuarryPlugin.class);

    private BlockLocation location;
    private boolean dirty = true;

    public static SmartInventory getInventory(BlockLocation location) {
        QuarryMenu provider = new QuarryMenu(location);
        plugin.getServer().getPluginManager().registerEvents(provider, plugin);
        return SmartInventory.builder()
                .id("quarryMenu")
                .closeable(true)
                .provider(provider)
                .title("Quarry")
                .type(InventoryType.CHEST)
                .size(1, 9)
                .listener(new InventoryListener<>(InventoryCloseEvent.class, e -> HandlerList.unregisterAll(provider)))
                .build();
    }

    private QuarryMenu(BlockLocation location) {
        this.location = location;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.update(player, contents);
    }

    @Override
    public void update(Player player, InventoryContents contents) {
        if (dirty) {
            dirty = false;
            Optional<QuarryData> quarryDataOptional = QuarryData.fromBlockLocation(location);
            if (!quarryDataOptional.isPresent()) {
                SmartInvsPlugin.manager().getInventory(player).ifPresent(inv -> inv.close(player));
            } else {
                QuarryData quarry = quarryDataOptional.get();
                ItemStack filterButton = new ItemStack(Material.BARRIER);
                ItemMeta fim = Objects.requireNonNull(filterButton.getItemMeta());
                fim.setDisplayName("Filters");
                filterButton.setItemMeta(fim);
                contents.add(ClickableItem.of(filterButton, e -> {
                    FilterMenu.getInventory(location).open(player);
                }));
                List<String> statsLore = Arrays.asList(
                        String.format("Blocks mined: %d", quarry.getBlocksMined()),
                        String.format("Blocks scanned: %d", quarry.getBlocksScanned()),
                        String.format("Blocks remaining: %d", quarry.getBlocksTotal() - quarry.getBlocksScanned())
                );
                if (QuarryRunner.isRunning(location)) {
                    ItemStack stopButton = new ItemStack(Material.RED_WOOL);
                    ItemMeta sim = Objects.requireNonNull(stopButton.getItemMeta());
                    sim.setDisplayName("Stop");
                    sim.setLore(statsLore);
                    contents.add(ClickableItem.of(stopButton, e -> QuarryRunner.stop(location)));
                } else {
                    ItemStack startButton = new ItemStack(Material.GREEN_WOOL);
                    ItemMeta sim = Objects.requireNonNull(startButton.getItemMeta());
                    sim.setDisplayName("Start");
                    sim.setLore(statsLore);
                    contents.add(ClickableItem.of(startButton, e -> QuarryRunner.start(location)));
                }
                ItemStack upgradeButton = new ItemStack(Material.NETHER_STAR);
                ItemMeta uim = Objects.requireNonNull(upgradeButton.getItemMeta());
                uim.setDisplayName("Upgrades");
                upgradeButton.setItemMeta(uim);
                contents.add(ClickableItem.of(upgradeButton, e -> {
                    // open upgrade menu
                }));
            }
        }
    }
}

