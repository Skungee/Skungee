package me.limeglass.skungee.objects;

import org.bukkit.OfflinePlayer;

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
