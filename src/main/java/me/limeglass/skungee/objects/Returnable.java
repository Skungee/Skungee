package me.limeglass.skungee.objects;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import me.limeglass.skungee.spigot.Skungee;
import me.limeglass.skungee.spigot.utils.Utils;

public interface Returnable {
	
	SkungeeReturnable returnable = Utils.getEnum(SkungeeReturnable.class, Skungee.getInstance().getConfig().getString("SkungeeReturn", "STRING"));
	
	@SuppressWarnings("deprecation")
	public default Object[] convert(SkungeePlayer... players) {
		Set<Object> converted = new HashSet<Object>();
		for (SkungeePlayer player : players) {
			switch (returnable) {
				case OFFLINEPLAYER:
					if (Skungee.getInstance().getConfig().getBoolean("SkungeeReturnUUID", false)) converted.add(Bukkit.getOfflinePlayer(player.getUUID()));
					else converted.add(Bukkit.getOfflinePlayer(player.getName()));
					break;
				case SKUNGEE:
					converted.add(player);
					break;
				case STRING:
					converted.add(player.getName());
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
			return converted.toArray(new SkungeePlayer[converted.size()]);
		}
	}
	
	public default Object[] convert(Set<SkungeePlayer> players) {
		return convert(players.toArray(new SkungeePlayer[players.size()]));
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
			return (value == 1) ? String.class : (value == 2) ? OfflinePlayer.class : SkungeePlayer.class;
		}
	}
}
