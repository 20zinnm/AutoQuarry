package tech.meyerzinn.autoquarry.util;

import com.google.gson.*;
import org.bukkit.Bukkit;

import java.lang.reflect.Type;
import java.util.UUID;

public class BlockLocationAdapter implements JsonDeserializer<BlockLocation>, JsonSerializer<BlockLocation> {
    @Override
    public BlockLocation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonPrimitive()) {
            throw new JsonParseException("not a JSON primitive");
        }

        final JsonPrimitive prim = (JsonPrimitive) json;
        if (!prim.isString()) {
            throw new JsonParseException("not a JSON string");
        }

        String[] parts = prim.getAsString().split(":");
        if (parts.length != 4) {
            throw new JsonParseException("invalid block location");
        }

        final UUID worldUID = UUID.fromString(parts[0]);
        final int x = Integer.valueOf(parts[1]);
        final int y = Integer.valueOf(parts[2]);
        final int z = Integer.valueOf(parts[3]);
        return new BlockLocation(Bukkit.getWorld(worldUID), x, y, z);
    }

    @Override
    public JsonElement serialize(BlockLocation location, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(String.format("%s:%d:%d:%d", location.world.getUID().toString(), location.x, location.y, location.z));
    }
}
