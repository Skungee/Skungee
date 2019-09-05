package me.limeglass.skungee.objects.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.limeglass.skungee.objects.packets.SkungeePacket;

/**
 * 
 * Called when sending a SkungeePacket to the Bungeecord.
 *
 */
public class SkungeeSendingEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	private final SkungeePacket packet;
	
	public SkungeeSendingEvent(SkungeePacket packet) {
		super(true);
		this.packet = packet;
	}
	
	public SkungeePacket getPacket() {
		return packet;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}