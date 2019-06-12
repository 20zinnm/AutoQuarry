package tech.meyerzinn.autoquarry.util;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.lang.reflect.Type;
import java.util.UUID;

public class OfflinePlayerAdapter implements JsonDeserializer<OfflinePlayer>, JsonSerializer<OfflinePlayer> {
    @Override
    public OfflinePlayer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Bukkit.getOfflinePlayer(UUID.fromString(json.getAsString()));
    }

    @Override
    public JsonElement serialize(OfflinePlayer src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.getUniqueId().toString());
    }
}
