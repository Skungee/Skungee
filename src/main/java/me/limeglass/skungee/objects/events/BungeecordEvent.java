package me.limeglass.skungee.objects.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.limeglass.skungee.objects.Returnable;
import me.limeglass.skungee.objects.SkungeePlayer;

public class BungeecordEvent extends Event implements Returnable {
	
	private static final HandlerList handlers = new HandlerList();
	private SkungeePlayer[] players;
	private String[] servers;

	public BungeecordEvent(String... servers) {
		this.servers = servers;
	}
	
	public BungeecordEvent(SkungeePlayer... players) {
		this.players = players;
	}
	
	public BungeecordEvent(String server, SkungeePlayer... players) {
		this.servers = new String[]{server};
		this.players = players;
	}

	public SkungeePlayer[] getPlayers() {
		return players;
	}
	
	public String[] getServers() {
		return servers;
	}
	
	public String getServer() {
		return servers[0];
	}
	
	public Object[] getConverted() {
		return convert(players);
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
