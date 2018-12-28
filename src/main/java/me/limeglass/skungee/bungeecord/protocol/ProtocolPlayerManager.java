package me.limeglass.skungee.bungeecord.protocol;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class ProtocolPlayerManager {
	
	private static final Map<UUID, ProtocolPlayer> players = Maps.newConcurrentMap();
	
	public static Optional<ProtocolPlayer> getPlayer(final UUID uniqueId) {
		return players.values().parallelStream()
				.filter(player -> player.getUniqueId().equals(uniqueId))
				.findFirst();
	}
	
	public static void addPlayer(ProtocolPlayer player) {
		if (!players.containsKey(player.getUniqueId()))
			players.put(player.getUniqueId(), player);
	}
	
	public static void removePlayer(UUID uniqueId) {
		Sets.newConcurrentHashSet(players.keySet()).parallelStream()
				.filter(uuid -> uuid.equals(uniqueId))
				.forEach(uuid -> players.remove(uuid));
	}
	
	public static Set<Entry<UUID, ProtocolPlayer>> getEntrySet() {
		return players.entrySet();
	}
	
	public static Map<UUID, ProtocolPlayer> getPlayerMap() {
		return players;
	}
	
	public static Collection<ProtocolPlayer> getPlayers() {
		return players.values();
	}
	
	public static Set<UUID> getUniqueIds() {
		return players.keySet();
	}
	
}