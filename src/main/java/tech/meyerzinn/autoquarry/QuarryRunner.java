package tech.meyerzinn.autoquarry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import tech.meyerzinn.autoquarry.event.QuarryDataUpdateEvent;
import tech.meyerzinn.autoquarry.util.BlockLocation;
import tech.meyerzinn.autoquarry.util.BlockLocationAdapter;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class QuarryRunner implements Runnable, Listener {

    private static final Map<BlockLocation, Integer> running = new HashMap<>();
    private static final JavaPlugin plugin = JavaPlugin.getPlugin(AutoQuarryPlugin.class);
    private static final Gson gson;

    static {
        gson = new GsonBuilder()
                .registerTypeAdapter(BlockLocation.class, new BlockLocationAdapter())
                .create();
    }

    public static void start(BlockLocation location) {
        // make sure it's not running
        stop(location);
        QuarryData.fromBlockLocation(location).ifPresent(qd -> {
            running.put(location, plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new QuarryRunner(location), (long) qd.getSpeed().ticks, qd.getSpeed().ticks));
            qd.doUpdate(false);
        });
    }

    public static void stop(BlockLocation loc) {
        if (running.containsKey(loc)) {
            plugin.getServer().getScheduler().cancelTask(running.get(loc));
            running.remove(loc);
            plugin.getServer().getPluginManager().callEvent(new QuarryDataUpdateEvent(loc, false));
        }
    }

    public static boolean isRunning(BlockLocation loc) {
        return running.containsKey(loc);
    }

    private BlockLocation location;

    public QuarryRunner(BlockLocation location) {
        this.location = location;
    }

    @Override
    public void run() {
        Optional<QuarryData> quarryDataOptional = QuarryData.fromBlockLocation(location);
        if (quarryDataOptional.isPresent()) {
            quarryDataOptional.get().step();
        } else {
            stop(location);
        }
    }

    @EventHandler
    public void onQuarryDataUpdate(QuarryDataUpdateEvent e) {
        if (e.getBlockLocation().equals(location) && e.isRestart()) {
            start(location); // restart quarry to handle changes to its state
        }
    }

    public static void save(File file) {
        try (FileWriter writer = new FileWriter(file, false)) {
            gson.toJson(running.keySet(), writer);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save running quarries. All quarries are stopped.");
            e.printStackTrace();
        }
    }

    public static void load(File file) {
        try (FileReader reader = new FileReader(file)) {
            BlockLocation[] run = gson.fromJson(reader, BlockLocation[].class);
            if (run == null) return;
            for (BlockLocation location : run) {
                start(location);
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Could not load running quarries. All quarries are stopped.");
            e.printStackTrace();
        }
    }
}
