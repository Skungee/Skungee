package me.limeglass.skungee.bungeecord.managers;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import me.limeglass.skungee.bungeecord.Skungee;
import me.limeglass.skungee.bungeecord.database.Database;
import me.limeglass.skungee.bungeecord.database.H2Database;
import me.limeglass.skungee.objects.SkungeePlayer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PlayerTimeManager {

	private Database<PlayerTime> database;

	public PlayerTimeManager(Skungee instance) {
		try {
			database = new H2Database<>(instance, "playtime", PlayerTime.class, new HashMap<>());
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		ProxyServer proxy = ProxyServer.getInstance();
		proxy.getScheduler().schedule(instance, () -> {
			proxy.getPlayers().forEach(player -> {
				String uuid = player.getUniqueId() + "";
				PlayerTime time = database.get(uuid, new PlayerTime(player.getUniqueId()));
				if (time == null) {
					time = new PlayerTime(player.getUniqueId());
					database.put(uuid, time);
					return;
				}
				time.increment(player.getServer().getInfo().getName());
				database.put(uuid, time);
			});
		}, 0, 1, TimeUnit.SECONDS);
	}

	public PlayerTime getPlayerTime(SkungeePlayer player) {
		return getPlayerTime(player.getUUID());
	}

	public PlayerTime getPlayerTime(ProxiedPlayer player) {
		return getPlayerTime(player.getUniqueId());
	}

	public PlayerTime getPlayerTime(UUID uuid) {
		return database.get(uuid + "", new PlayerTime(uuid));
	}

	public static class PlayerTime {

		private final Map<String, Integer> times = new HashMap<>();
		private final UUID uuid;

		public PlayerTime(UUID uuid) {
			this.uuid = uuid;
		}

		public PlayerTime(UUID uuid, Map<String, Integer> existing) {
			this.uuid = uuid;
			times.putAll(existing);
		}

		public void increment(String server) {
			int time = times.getOrDefault(server, 0);
			times.put(server, time + 1);
		}

		public int getSeconds(String server) {
			return times.getOrDefault(server, 0);
		}

		public Set<Integer> getSeconds(String... servers) {
			return Arrays.stream(servers)
					.map(server -> times.getOrDefault(server, 0))
					.collect(Collectors.toSet());
		}

		public int getTotal() {
			return times.values().stream().mapToInt(Integer::intValue).sum();
		}

		public UUID getUniqueId() {
			return uuid;
		}

		public Map<String, Integer> getTimes() {
			return times;
		}

	}

}
