package tech.meyerzinn.autoquarry.menu;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.InventoryListener;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.SmartInvsPlugin;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.java.JavaPlugin;
import tech.meyerzinn.autoquarry.AutoQuarryPlugin;
import tech.meyerzinn.autoquarry.QuarryData;
import tech.meyerzinn.autoquarry.event.QuarryUpgradeEvent;
import tech.meyerzinn.autoquarry.upgrade.Fortune;
import tech.meyerzinn.autoquarry.upgrade.FuelCapacity;
import tech.meyerzinn.autoquarry.upgrade.MiningRadius;
import tech.meyerzinn.autoquarry.upgrade.MiningSpeed;
import tech.meyerzinn.autoquarry.util.BlockLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class UpgradeMenu implements InventoryProvider, Listener {

    private static final JavaPlugin plugin = JavaPlugin.getPlugin(AutoQuarryPlugin.class);

    private BlockLocation location;
    private boolean dirty = true;

    public SmartInventory getInventory(BlockLocation location) {
        UpgradeMenu provider = new UpgradeMenu(location);
        return SmartInventory.builder()
                .id("quarryUpgradeMenu")
                .closeable(true)
                .provider(provider)
                .title("Upgrade")
                .type(InventoryType.CHEST)
                .size(1, 9)
                .listener(new InventoryListener<>(InventoryCloseEvent.class, e -> HandlerList.unregisterAll(provider)))
                .build();
    }

    private UpgradeMenu(BlockLocation location) {
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
                List<ClickableItem> items = new ArrayList<>();
                if (quarry.getFortune().next() != null) {
                    Fortune next = quarry.getFortune().next();
                    Objects.requireNonNull(next);
                    items.add(ClickableItem.of(next.getIcon(), e -> {
                        EconomyResponse resp = AutoQuarryPlugin.econ.withdrawPlayer(player, next.cost);
                        if (resp.type == EconomyResponse.ResponseType.SUCCESS) {
                            quarry.setFortune(next);
                        } else {
                            SmartInvsPlugin.manager().getInventory(player).ifPresent(inv -> inv.close(player));
                            player.sendMessage(ChatColor.RED + "Error upgrading quarry: " + resp.errorMessage);
                        }
                    }));
                }
                if (quarry.getSpeed().next() != null) {
                    MiningSpeed next = quarry.getSpeed().next();
                    Objects.requireNonNull(next);
                    items.add(ClickableItem.of(next.getIcon(), e -> {
                        EconomyResponse resp = AutoQuarryPlugin.econ.withdrawPlayer(player, next.cost);
                        if (resp.type == EconomyResponse.ResponseType.SUCCESS) {
                            quarry.setSpeed(next);
                        } else {
                            SmartInvsPlugin.manager().getInventory(player).ifPresent(inv -> inv.close(player));
                            player.sendMessage(ChatColor.RED + "Error upgrading quarry: " + resp.errorMessage);
                        }
                    }));
                }
                if (quarry.getSize().next() != null) {
                    MiningRadius next = quarry.getSize().next();
                    Objects.requireNonNull(next);
                    items.add(ClickableItem.of(next.getIcon(), e -> {
                        EconomyResponse resp = AutoQuarryPlugin.econ.withdrawPlayer(player, next.cost);
                        if (resp.type == EconomyResponse.ResponseType.SUCCESS) {
                            quarry.setSize(next);
                        } else {
                            SmartInvsPlugin.manager().getInventory(player).ifPresent(inv -> inv.close(player));
                            player.sendMessage(ChatColor.RED + "Error upgrading quarry: " + resp.errorMessage);
                        }
                    }));
                }
                if (quarry.getFuelCapacity().next() != null) {
                    FuelCapacity next = quarry.getFuelCapacity().next();
                    Objects.requireNonNull(next);
                    items.add(ClickableItem.of(next.getIcon(), e -> {
                        EconomyResponse resp = AutoQuarryPlugin.econ.withdrawPlayer(player, next.cost);
                        if (resp.type == EconomyResponse.ResponseType.SUCCESS) {
                            quarry.setFuelCapacity(next);
                        } else {
                            SmartInvsPlugin.manager().getInventory(player).ifPresent(inv -> inv.close(player));
                            player.sendMessage(ChatColor.RED + "Error upgrading quarry: " + resp.errorMessage);
                        }
                    }));
                }
                for (ClickableItem item : items) {
                    contents.add(item);
                }
            }
        }
    }

    @EventHandler
    public void onQuarryUpgrade(QuarryUpgradeEvent e) {
        dirty = true;
    }
}
