package me.limeglass.skungee.objects;

import java.io.Serializable;
import java.util.UUID;

import org.eclipse.jdt.annotation.Nullable;

public class SkungeePlayer implements Serializable {

	private static final long serialVersionUID = -6722933162709325087L;
	private final UUID uuid;
	private final String name;
	private Boolean isBukkitPlayer;

	public SkungeePlayer(Boolean isBukkitPlayer, @Nullable UUID uuid, String name) {
		this.uuid = uuid;
		this.name = name;
		this.isBukkitPlayer = isBukkitPlayer;
	}
	
	public void setBukkitPlayer(Boolean isBukkitPlayer) {
		this.isBukkitPlayer = isBukkitPlayer;
	}
	
	public Boolean isBukkitPlayer() {
		return isBukkitPlayer;
	}
	
	public UUID getUUID() {
		return uuid;
	}
	
	public String getName() {
		return name;
	}
}
