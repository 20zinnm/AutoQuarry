package tech.meyerzinn.autoquarry.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import tech.meyerzinn.autoquarry.util.BlockLocation;

/*
Called when quarry data is modified, so that inventory menus can be refreshed.
If restart is true, the quarry should be restarted to account for the change.
 */
public class QuarryDataUpdateEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private BlockLocation blockLocation;
    private boolean restart;

    public QuarryDataUpdateEvent(BlockLocation quarryLocation, boolean restart) {
        this.blockLocation = quarryLocation;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public BlockLocation getBlockLocation() {
        return this.blockLocation;
    }

    public boolean isRestart() {
        return restart;
    }
}
