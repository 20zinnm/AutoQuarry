package tech.meyerzinn.autoquarry;

import com.google.common.collect.ImmutableMap;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import tech.meyerzinn.autoquarry.event.QuarryListener;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public final class AutoQuarryPlugin extends JavaPlugin implements Listener {

    public static Economy econ = null;
    public static Permission perm = null;

    public static ImmutableMap<Material, Boolean> defaultFilters;
    public static ImmutableMap<Material, Integer> fuels;
    private File quarriesFile = new File(getDataFolder(), "quarries.json");

    @Override
    public void onEnable() {
        if (!setupEconomy() || !setupPermissions()) {
            getLogger().severe("AutoQuarryPlugin requires Vault to operate.");
            return;
        }

        saveDefaultConfig();

        // load materials/default filters
        ConfigurationSection filtersSection = getConfig().getConfigurationSection("materials");
        ImmutableMap.Builder<Material, Boolean> defaultFiltersBuilder = ImmutableMap.builder();
        if (filtersSection != null) {
            for (String key : filtersSection.getKeys(false)) {
                try {
                    Material material = Material.getMaterial(key);
                    if (material == null) throw new IllegalArgumentException();
                    defaultFiltersBuilder = defaultFiltersBuilder.put(material, filtersSection.getBoolean(key));
                } catch (IllegalArgumentException e) {
                    getLogger().warning(String.format("Unrecognized material: %s", key));
                }
            }
        } else {
            getLogger().warning("Configuration section for materials and filters is empty. Quarries will be unable to mine any blocks.");
        }
        defaultFilters = defaultFiltersBuilder.build();

        // load fuels
        ConfigurationSection fuelsSection = getConfig().getConfigurationSection("fuels");
        ImmutableMap.Builder<Material, Integer> fuelsBuilder = ImmutableMap.builder();
        if (fuelsSection != null) {
            for (String key : fuelsSection.getKeys(false)) {
                try {
                    Material material = Material.getMaterial(key);
                    if (material == null) throw new IllegalArgumentException();
                    fuelsBuilder = fuelsBuilder.put(material, fuelsSection.getInt(key));
                } catch (IllegalArgumentException e) {
                    getLogger().warning(String.format("Unrecognized fuel material: %s", key));
                }
            }
        } else {
            getLogger().warning("Configuration section for fuels is empty. Players will be unable to refuel quarries.");
        }
        fuels = fuelsBuilder.build();

        // load quarries
        QuarryRunner.load(quarriesFile);
        // register event manager
        getServer().getPluginManager().registerEvents(new QuarryListener(), this);
    }

    @Override
    public void onDisable() {
        QuarryRunner.save(quarriesFile);
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        econ = rsp.getProvider();
        return true;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            perm = permissionProvider.getProvider();
        }
        return (perm != null);
    }

}