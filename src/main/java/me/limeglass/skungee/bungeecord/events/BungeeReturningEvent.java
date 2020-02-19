package me.limeglass.skungee.bungeecord.events;

import java.net.InetAddress;

import me.limeglass.skungee.common.packets.ServerPacket;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

/**
 * 
 * Called when a SkungeePacket is requesting a returned value.
 *
 */
public class BungeeReturningEvent extends Event implements Cancellable {

	private final ServerPacket packet;
	private final InetAddress address;
	private boolean cancelled;
	private Object object;

	public BungeeReturningEvent(ServerPacket packet, Object object, InetAddress address) {
		this.address = address;
		this.packet = packet;
		this.object = object;
	}

	public ServerPacket getPacket() {
		return packet;
	}

	/**
	 * @return The address the received SkungeePacket came from.
	 */
	public InetAddress getAddress() {
		return address;
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

}
