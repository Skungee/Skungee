package me.limeglass.skungee.bungeecord.database.serializers;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;

import me.limeglass.skungee.bungeecord.database.Serializer;
import me.limeglass.skungee.bungeecord.managers.PlayerTimeManager.PlayerTime;

public class PlayerTimeSerializer implements Serializer<PlayerTime> {

	@Override
	public JsonElement serialize(PlayerTime time, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject json = new JsonObject();
		if (time == null)
			return json;
		json.addProperty("uuid", time.getUniqueId() + "");
		JsonArray times = new JsonArray();
		time.getTimes().entrySet().forEach(entry -> {
			JsonObject object = new JsonObject();
			object.addProperty("server", entry.getKey());
			object.addProperty("time", entry.getValue());
			times.add(object);
		});
		json.add("times", times);
		return json;
	}

	@Override
	public PlayerTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject object = json.getAsJsonObject();
		String stringUUID = object.get("uuid").getAsString();
		if (stringUUID == null)
			return null;
		UUID uuid = UUID.fromString(stringUUID);
		if (uuid == null)
			return null;
		Map<String, Integer> times = new HashMap<>();
		JsonElement timesElement = object.get("times");
		if (timesElement != null && !timesElement.isJsonNull() && timesElement.isJsonArray()) {
			JsonArray array = timesElement.getAsJsonArray();
			array.forEach(element -> {
				JsonObject elementObject = element.getAsJsonObject();
				JsonElement serverElement = elementObject.get("server");
				if (serverElement == null || serverElement.isJsonNull())
					return;
				String server = serverElement.getAsString();
				JsonElement timeElement = elementObject.get("time");
				if (timeElement == null || timeElement.isJsonNull())
					return;
				times.put(server, timeElement.getAsInt());
			});
		}
		return new PlayerTime(uuid, times);
	}

}
