package me.limeglass.skungee.objects.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.limeglass.skungee.objects.packets.SkungeePacket;

/**
 * 
 * Called when sending a SkungeePacket to the Bungeecord.
 *
 */
public class SkungeeSendingEvent extends Event implements Cancellable {
	
	private static final HandlerList handlers = new HandlerList();
	private final SkungeePacket packet;
	private boolean cancelled;
	
	public SkungeeSendingEvent(SkungeePacket packet) {
		this.packet = packet;
	}
	
	public SkungeePacket getPacket() {
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