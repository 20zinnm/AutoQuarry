package tech.meyerzinn.autoquarry.util;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Objects;

public class BlockLocation {
    public final World world;
    public final int x;
    public final int y;
    public final int z;

    public BlockLocation(World world, int x, int y, int z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public BlockLocation(Location location) {
        this(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public Location toLocation() {
        return new Location(world, (double) x, (double) y, (double) z);
    }

    public Block getBlock() {
        return world.getBlockAt(x, y, z);
    }

//    public BlockLocation[] getNeighbors() {
//        final int[] xoff = {-1, 0, 1, 0};
//        final int[] zoff = {0, 1, 0, -1};
//
//        BlockLocation[] neighbors = new BlockLocation[4];
//        for (int i = 0; i < 4; i++) {
//            neighbors[i] = new BlockLocation(world, x + xoff[i], y, z + zoff[i]);
//        }
//        return neighbors;
//    }

    public BlockLocation getBelow() {
        return new BlockLocation(world, x, y - 1, z);
    }

    public BlockLocation getAbove() {
        return new BlockLocation(world, x, y + 1, z);
    }

    public BlockLocation[] getNeighbors() {
        BlockLocation[] locations = new BlockLocation[4];
        final int dx[] = {1, 0, -1, 0};
        final int dz[] = {0, 1, 0, -1};
        for (int i = 0; i < 4; i++) {
            locations[i] = new BlockLocation(world, x + dx[i], y, z + dz[i]);
        }
        return locations;
    }

    @Override
    public int hashCode() {
        return Objects.hash(world.getUID(), x, y, z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockLocation that = (BlockLocation) o;
        return x == that.x &&
                y == that.y &&
                z == that.z &&
                world.getUID().equals(that.world.getUID());
    }
}
