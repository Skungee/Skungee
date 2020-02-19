package me.limeglass.skungee.bungeecord.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BungeecordEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	private String server;

	public BungeecordEvent(String server) {
		super(true);
		this.server = server;
	}
	
	public String getServer() {
		return server;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
