package tech.meyerzinn.autoquarry.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import tech.meyerzinn.autoquarry.QuarryData;

public class QuarryDataType implements PersistentDataType<String, QuarryData> {

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(BlockLocation.class, new BlockLocationAdapter())
            .create();

    @Override
    public Class<String> getPrimitiveType() {
        return String.class;
    }

    @Override
    public Class<QuarryData> getComplexType() {
        return QuarryData.class;
    }

    @Override
    public String toPrimitive(QuarryData complex, PersistentDataAdapterContext context) {
        return gson.toJson(complex);
    }

    @Override
    public QuarryData fromPrimitive(String primitive, PersistentDataAdapterContext context) {
        return gson.fromJson(primitive, QuarryData.class);
    }
}
