package me.limeglass.skungee.bungeecord.events;

import me.limeglass.skungee.common.objects.SkungeeServer;
import me.limeglass.skungee.common.packets.ProxyPacket;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

/**
 * 
 * Called when sending a BungeePacket to a Spigot server.
 *
 */
public class BungeeSendingEvent extends Event implements Cancellable {
	
	private final SkungeeServer server;
	private final ProxyPacket packet;
	private boolean cancelled;
	
	public BungeeSendingEvent(ProxyPacket packet, SkungeeServer server) {
		this.server = server;
		this.packet = packet;
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
	
	@Override
	public boolean isCancelled() {
		return cancelled;
	}
	
	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

}