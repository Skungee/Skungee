package me.limeglass.skungee.spigot.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.limeglass.skungee.common.packets.ServerPacket;

/**
 * 
 * Called when the returned value from Bungeecord comes back from the SkungeePacket.
 *
 */
public class SkungeeReturnedEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private final ServerPacket packet;
	private Object object;

	public SkungeeReturnedEvent(ServerPacket packet, Object object) {
		super(true);
		this.packet = packet;
		this.object = object;
	}

	public ServerPacket getPacket() {
		return packet;
	}

	public Object getObject() {
		return object;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
