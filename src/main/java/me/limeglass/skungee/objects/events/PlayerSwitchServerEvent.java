package me.limeglass.skungee.objects.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.limeglass.skungee.objects.Returnable;
import me.limeglass.skungee.objects.SkungeePlayer;

public class PlayerSwitchServerEvent extends Event implements Returnable {
	
	private static final HandlerList handlers = new HandlerList();
	private final SkungeePlayer player;
	private final String server;
	
	public PlayerSwitchServerEvent(String server, SkungeePlayer player) {
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