package me.limeglass.skungee.objects.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.limeglass.skungee.objects.packets.BungeePacket;

/**
 * 
 * Called when a BungeePacket has been received from Bungeecord.
 *
 */
public class SkungeeReceivedEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private final BungeePacket packet;
	private boolean cancelled;
	
	public SkungeeReceivedEvent(BungeePacket packet) {
		this.packet = packet;
	}
	
	public BungeePacket getPacket() {
		return packet;
	}
	
	@Override
	public boolean isCancelled() {
		return cancelled;
	}
	
	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
	
}
