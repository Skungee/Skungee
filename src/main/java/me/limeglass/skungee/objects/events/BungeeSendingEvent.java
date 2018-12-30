package me.limeglass.skungee.objects.events;

import me.limeglass.skungee.objects.ConnectedServer;
import me.limeglass.skungee.objects.packets.BungeePacket;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

/**
 * 
 * Called when sending a BungeePacket to a Spigot server.
 *
 */
public class BungeeSendingEvent extends Event implements Cancellable {
	
	private final ConnectedServer server;
	private final BungeePacket packet;
	private boolean cancelled;
	
	public BungeeSendingEvent(BungeePacket packet, ConnectedServer server) {
		this.server = server;
		this.packet = packet;
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
	
	@Override
	public boolean isCancelled() {
		return cancelled;
	}
	
	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

}