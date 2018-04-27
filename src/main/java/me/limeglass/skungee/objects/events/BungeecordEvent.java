package me.limeglass.skungee.objects.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.limeglass.skungee.objects.Returnable;
import me.limeglass.skungee.objects.SkungeePlayer;

public class BungeecordEvent extends Event implements Returnable {
	
	private static final HandlerList handlers = new HandlerList();
	private SkungeePlayer player = null;
	private String server = null;

	public BungeecordEvent(String server) {
		this.server = server;
	}
	
	public BungeecordEvent(SkungeePlayer player) {
		this.player = player;
	}
	
	public BungeecordEvent(String server, SkungeePlayer player) {
		this.server = server;
		this.player = player;
	}

	public SkungeePlayer getPlayer() {
		return player;
	}
	
	public String getServer() {
		return server;
	}
	
	public Object[] getConverted() {
		return convert(player);
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
