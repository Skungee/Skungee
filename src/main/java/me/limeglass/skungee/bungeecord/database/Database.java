package me.limeglass.skungee.bungeecord.database;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import me.limeglass.skungee.bungeecord.database.serializers.PlayerTimeSerializer;
import me.limeglass.skungee.bungeecord.managers.PlayerTimeManager.PlayerTime;

public abstract class Database<T> {

	protected final Gson gson;
	private final GsonBuilder builder = new GsonBuilder()
			.registerTypeAdapter(PlayerTime.class, new PlayerTimeSerializer())
			.excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.STATIC)
			.enableComplexMapKeySerialization()
			.serializeNulls();

	public Database(Map<Type, Serializer<?>> serializers) {
		serializers.forEach((type, serializer) -> builder.registerTypeAdapter(type, serializer));
		gson = builder.create();
	}

	public Database() {
		gson = builder.create();
	}

	public abstract void put(String key, T value);

	public abstract T get(String key, T def);

	public abstract boolean has(String key);

	public abstract Set<String> getKeys();

	public T get(String key) {
		return get(key, null);
	}

	public void delete(String key) {
		put(key, null);
	}

	public abstract void clear();

	public String serialize(Object object, Type type) {
		return gson.toJson(object, type);
	}

	public Object deserialize(String json, Type type) {
		return gson.fromJson(json, type);
	}

}
