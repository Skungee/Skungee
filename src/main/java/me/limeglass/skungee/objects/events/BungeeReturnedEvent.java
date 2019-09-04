package me.limeglass.skungee.objects.events;

import me.limeglass.skungee.objects.ConnectedServer;
import me.limeglass.skungee.objects.packets.BungeePacket;
import net.md_5.bungee.api.plugin.Event;

/**
 * 
 * Called when the returned value from Spigot comes back from the BungeePacket.
 *
 */
public class BungeeReturnedEvent extends Event {

	private final ConnectedServer server;
	private final BungeePacket packet;
	private Object object;

	public BungeeReturnedEvent(BungeePacket packet, Object object, ConnectedServer server) {
		this.server = server;
		this.packet = packet;
		this.object = object;
	}

	/**
	 * The server that the BungeePacket is being sent too.
	 * 
	 * @return The ConnectedServer that is being sent.
	 */
	public ConnectedServer getConnectedServer() {
		return server;
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

}
