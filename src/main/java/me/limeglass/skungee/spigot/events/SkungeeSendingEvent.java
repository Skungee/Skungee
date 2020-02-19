package me.limeglass.skungee.spigot.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.limeglass.skungee.common.packets.ServerPacket;

/**
 * 
 * Called when sending a SkungeePacket to the Bungeecord.
 *
 */
public class SkungeeSendingEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	private final ServerPacket packet;
	
	public SkungeeSendingEvent(ServerPacket packet) {
		super(true);
		this.packet = packet;
	}
	
	public ServerPacket getPacket() {
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