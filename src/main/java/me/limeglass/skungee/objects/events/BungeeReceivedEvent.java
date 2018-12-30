package me.limeglass.skungee.objects.events;

import java.net.InetAddress;

import me.limeglass.skungee.objects.packets.SkungeePacket;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

/**
 * 
 * Called when a SkungeePacket has been received from a Spigot server.
 *
 */
public class BungeeReceivedEvent extends Event implements Cancellable {

	private final SkungeePacket packet;
	private final InetAddress address;
	private boolean cancelled;
	
	public BungeeReceivedEvent(SkungeePacket packet, InetAddress address) {
		this.address = address;
		this.packet = packet;
	}
	
	public SkungeePacket getPacket() {
		return packet;
	}
	
	/**
	 * @return The address the received SkungeePacket came from.
	 */
	public InetAddress getAddress() {
		return address;
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
