package me.limeglass.skungee.common.objects;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import me.limeglass.skungee.common.player.PacketPlayer;
import me.limeglass.skungee.spigot.SkungeeSpigot;
import me.limeglass.skungee.spigot.utils.Utils;

public interface Returnable {

	SkungeeReturnable returnable = Utils.getEnum(SkungeeReturnable.class, SkungeeSpigot.getInstance().getConfig().getString("SkungeeReturn", "STRING"));

	@SuppressWarnings("deprecation")
	public default Object[] convert(SkungeeReturnable type, PacketPlayer... players) {
		Set<Object> converted = new HashSet<Object>();
		for (PacketPlayer player : players) {
			if (player == null)
				continue;
			switch (type) {
				case OFFLINEPLAYER:
					OfflinePlayer offline = Bukkit.getOfflinePlayer(player.getUUID());
					if (SkungeeSpigot.getInstance().getConfig().getBoolean("SkungeeReturnUUID", false) && offline != null) {
						converted.add(Bukkit.getOfflinePlayer(player.getUUID()));
					} else {
						converted.add(Bukkit.getOfflinePlayer(player.getUsername()));
					}
					break;
				case SKUNGEE:
					converted.add(player);
					break;
				case STRING:
					converted.add(player.getUsername());
					break;
				case UUID:
					converted.add(player.getUUID().toString());
					break;
			}
		}
		if (returnable.getReturnType() == String.class) {
			return converted.toArray(new String[converted.size()]);
		} else if (returnable.getReturnType() == OfflinePlayer.class) {
			return converted.toArray(new OfflinePlayer[converted.size()]);
		} else {
			return converted.toArray(new PacketPlayer[converted.size()]);
		}
	}
	
	public default Object[] convert(Set<PacketPlayer> players) {
		return convert(players.toArray(new PacketPlayer[players.size()]));
	}
	
	public default Object[] convert(PacketPlayer... players) {
		return convert(returnable, players);
	}
	
	public static Class<?> getReturnType() {
		return (returnable == null) ? String.class : returnable.getReturnType();
	}
	
	public enum SkungeeReturnable {
		
		STRING(1),
		UUID(1),
		OFFLINEPLAYER(2),
		SKUNGEE(3);
		
		private int value;

		private SkungeeReturnable(int value) {
			this.value = value;
		}
		
		public Class<?> getReturnType() {
			return (value == 1) ? String.class : (value == 2) ? OfflinePlayer.class : PacketPlayer.class;
		}
	}
}
