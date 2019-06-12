package tech.meyerzinn.autoquarry;

import net.milkbowl.vault.economy.Economy;
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
    public static Map<Material, Boolean> defaultFilters = new HashMap<>();
    private File quarriesFile = new File(getDataFolder(), "quarries.json");

    @Override
    public void onEnable() {
        if (!setupEconomy()) {
            getLogger().severe("AutoQuarryPlugin requires Vault to operate.");
            return;
        }
        // load default filters from config
        saveDefaultConfig();

        ConfigurationSection filtersSection = getConfig().getConfigurationSection("materials");
        if (filtersSection != null) {
            for (String key : filtersSection.getKeys(false)) {
                try {
                    Material material = Material.getMaterial(key);
                    defaultFilters.put(material, filtersSection.getBoolean(key, true));
                } catch (IllegalArgumentException e) {
                    getLogger().warning(String.format("Unrecognized material: %s", key));
                }
            }
        }
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
}