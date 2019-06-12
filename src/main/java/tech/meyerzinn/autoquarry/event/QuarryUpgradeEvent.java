package tech.meyerzinn.autoquarry.event;

import tech.meyerzinn.autoquarry.util.BlockLocation;

public class QuarryUpgradeEvent extends QuarryDataUpdateEvent {
    public QuarryUpgradeEvent(BlockLocation quarryLocation, boolean restart) {
        super(quarryLocation, restart);
    }
}
