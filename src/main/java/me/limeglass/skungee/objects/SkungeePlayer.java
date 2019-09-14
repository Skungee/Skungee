package me.limeglass.skungee.objects;

import java.io.Serializable;
import java.util.UUID;

import org.eclipse.jdt.annotation.Nullable;

public class SkungeePlayer implements Serializable {

	private static final long serialVersionUID = -6722933162709325087L;
	private final String name;
	private final UUID uuid;
	private boolean bukkit;

	public SkungeePlayer(boolean bukkit, @Nullable UUID uuid, String name) {
		this.bukkit = bukkit;
		this.uuid = uuid;
		this.name = name;
	}

	public void setBukkitPlayer(boolean bukkit) {
		this.bukkit = bukkit;
	}

	public Boolean isBukkitPlayer() {
		return bukkit;
	}

	public String getName() {
		return name;
	}

	public UUID getUUID() {
		return uuid;
	}

	public String toString() {
		return (uuid != null) ? name + ":" + uuid : name;
	}

}
