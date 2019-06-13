package tech.meyerzinn.autoquarry.menu;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.InventoryListener;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.SmartInvsPlugin;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import tech.meyerzinn.autoquarry.AutoQuarryPlugin;
import tech.meyerzinn.autoquarry.QuarryData;
import tech.meyerzinn.autoquarry.event.QuarryDataUpdateEvent;
import tech.meyerzinn.autoquarry.util.BlockLocation;

import java.util.*;

public class FilterMenu implements InventoryProvider, Listener {

    private BlockLocation location;
    private boolean dirty = true;
    private static final JavaPlugin plugin = JavaPlugin.getPlugin(AutoQuarryPlugin.class);

    public static SmartInventory getInventory(BlockLocation location) {
        FilterMenu provider = new FilterMenu(location);
        return SmartInventory.builder()
                .id("quarryFilterMenu")
                .provider(provider)
                .size(3, 9)
                .title("Filters")
                .closeable(true)
                .listener(new InventoryListener<>(InventoryCloseEvent.class, e -> HandlerList.unregisterAll(provider)))
                .build();
    }

    private FilterMenu(BlockLocation location) {
        this.location = location;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
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
                List<ClickableItem> items = new ArrayList<>();

                Pagination pagination = contents.pagination();
                pagination.setItemsPerPage(7);

                for (Material mat : AutoQuarryPlugin.defaultFilters.keySet()) {
                    ItemStack item = new ItemStack(mat);
                    ItemMeta im = item.getItemMeta();
                    Objects.requireNonNull(im);
                    if (quarry.shouldMine(mat)) {
                        im.setDisplayName(ChatColor.GREEN + "INCLUDED");
                    } else {
                        im.setDisplayName(ChatColor.RED + "EXCLUDED");
                    }
                    im.setLore(Arrays.asList("Click here to toggle whether", "the quarry will mine this block."));
                    item.setItemMeta(im);
                    if (quarry.shouldMine(mat)) {
                        items.add(ClickableItem.of(item, e -> quarry.toggleFilter(mat)));
                    } else {
                        items.add(ClickableItem.of(item, e -> quarry.toggleFilter(mat)));
                    }
                }
                pagination.setItems(items.toArray(new ClickableItem[AutoQuarryPlugin.defaultFilters.size()]));
                pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 1));

                if (!pagination.isFirst()) {
                    ItemStack prevButton = new ItemStack(Material.ARROW);
                    ItemMeta im = Objects.requireNonNull(prevButton.getItemMeta());
                    im.setDisplayName("Previous Page");
                    prevButton.setItemMeta(im);
                    Objects.requireNonNull(prevButton.getItemMeta()).setDisplayName("Previous Page");
                    contents.set(2, 3, ClickableItem.of(prevButton,
                            e -> getInventory(location).open(player, pagination.previous().getPage())));
                }
                if (!pagination.isLast()) {
                    ItemStack nextButton = new ItemStack(Material.ARROW);
                    ItemMeta im = Objects.requireNonNull(nextButton.getItemMeta());
                    im.setDisplayName("Next Page");
                    nextButton.setItemMeta(im);
                    Objects.requireNonNull(nextButton.getItemMeta()).setDisplayName("Next Page");
                    contents.set(2, 5, ClickableItem.of(nextButton,
                            e -> getInventory(location).open(player, pagination.next().getPage())));
                }

                ItemStack doneButton = new ItemStack(Material.GREEN_WOOL);
                ItemMeta im = Objects.requireNonNull(doneButton.getItemMeta());
                im.setDisplayName("Done");
                doneButton.setItemMeta(im);
                contents.set(2, 4, ClickableItem.of(doneButton,
                        e -> QuarryMenu.getInventory(location).open(player)));
            }
        }
    }

    @EventHandler
    public void onQuarryDataUpdate(QuarryDataUpdateEvent e) {
        dirty = true;
    }
}
