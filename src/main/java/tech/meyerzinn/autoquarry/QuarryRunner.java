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
        QuarryData.fromBlockLocation(location).ifPresent(qd -> running.put(location, plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new QuarryRunner(location), (long) 0F, qd.getSpeed().ticks)));
    }

    public static void stop(BlockLocation loc) {
        if (running.containsKey(loc)) {
            plugin.getServer().getScheduler().cancelTask(running.get(loc));
            running.remove(loc);
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
        try {
            gson.toJson(running.keySet(), new FileWriter(file, false));
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save running quarries. All quarries are stopped.");
            e.printStackTrace();
        }
    }

    public static void load(File file) {
        try {
            BlockLocation[] run = gson.fromJson(new FileReader(file), BlockLocation[].class);
            for (BlockLocation location : run) {
                start(location);
            }
        } catch (FileNotFoundException e) {
            plugin.getLogger().severe("Could not load running quarries. All quarries are stopped.");
            e.printStackTrace();
        }
    }
}
