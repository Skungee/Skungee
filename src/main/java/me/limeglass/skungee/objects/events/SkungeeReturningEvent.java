package me.limeglass.skungee.objects.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.limeglass.skungee.objects.packets.BungeePacket;

/**
 * 
 * Called when a BungeePacket is requesting a returned value.
 *
 */
public class SkungeeReturningEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private final BungeePacket packet;
	private boolean cancelled;
	private Object object;
	
	public SkungeeReturningEvent(BungeePacket packet, Object object) {
		super(true);
		this.packet = packet;
		this.object = object;
	}
	
	public BungeePacket getPacket() {
		return packet;
	}
	
	public Object getObject() {
		return object;
	}
	
	public void setObject(Object object) {
		this.object = object;
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
