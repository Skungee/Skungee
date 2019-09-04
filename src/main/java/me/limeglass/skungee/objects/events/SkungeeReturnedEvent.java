package me.limeglass.skungee.objects.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.limeglass.skungee.objects.packets.SkungeePacket;

/**
 * 
 * Called when the returned value from Bungeecord comes back from the SkungeePacket.
 *
 */
public class SkungeeReturnedEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private final SkungeePacket packet;
	private Object object;

	public SkungeeReturnedEvent(SkungeePacket packet, Object object) {
		super(true);
		this.packet = packet;
		this.object = object;
	}

	public SkungeePacket getPacket() {
		return packet;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
