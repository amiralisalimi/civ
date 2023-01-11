package ir.ap.controller;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public interface JsonResponsor {
    Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
                private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

                @Override
                public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return LocalDateTime.parse(json.getAsString(), formatter);
                }
            })
            .registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
                private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

                @Override
                public JsonElement serialize(LocalDateTime localDateTime, Type srcType, JsonSerializationContext context) {
                    return new JsonPrimitive(formatter.format(localDateTime));
                }
            })
            .create();

    JsonObject JSON_EMPTY = GSON.fromJson("{}", JsonObject.class);
    JsonObject JSON_FALSE = GSON.fromJson("{\"ok\":false}", JsonObject.class);
    JsonObject JSON_TRUE = GSON.fromJson("{\"ok\":true}", JsonObject.class);

    default String toJsonStr(JsonObject jsonObj) {
        if (jsonObj == null)
            jsonObj = new JsonObject();
        return GSON.toJson(jsonObj);
    }

    default JsonObject toJsonObj(String jsonStr) {
        if (jsonStr == null)
            jsonStr = new String();
        return GSON.fromJson(jsonStr, JsonObject.class);
    }

    default JsonObject setOk(JsonObject jsonObj, boolean value) {
        if (jsonObj == null)
            jsonObj = new JsonObject();
        jsonObj.addProperty("ok", value);
        return jsonObj;
    }

    default boolean isOk(JsonObject jsonObj) {
        return jsonObj != null && jsonObj.has("ok") && jsonObj.get("ok").getAsBoolean();
    }

    default JsonObject messageToJsonObj(Object msg, boolean ok) {
        JsonObject jsonObj = new JsonObject();
        if (msg != null)
            jsonObj.addProperty("msg", msg.toString());
        jsonObj.addProperty("ok", ok);
        return jsonObj;
    }
}
