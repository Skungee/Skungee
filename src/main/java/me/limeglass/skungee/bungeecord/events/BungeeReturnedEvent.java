package me.limeglass.skungee.bungeecord.events;

import me.limeglass.skungee.common.objects.SkungeeServer;
import me.limeglass.skungee.common.packets.ProxyPacket;
import net.md_5.bungee.api.plugin.Event;

/**
 * 
 * Called when the returned value from Spigot comes back from the BungeePacket.
 *
 */
public class BungeeReturnedEvent extends Event {

	private final SkungeeServer server;
	private final ProxyPacket packet;
	private Object object;

	public BungeeReturnedEvent(ProxyPacket packet, Object object, SkungeeServer server) {
		this.server = server;
		this.packet = packet;
		this.object = object;
	}

	/**
	 * The server that the BungeePacket is being sent too.
	 * 
	 * @return The ConnectedServer that is being sent.
	 */
	public SkungeeServer getConnectedServer() {
		return server;
	}

	public ProxyPacket getPacket() {
		return packet;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

}
