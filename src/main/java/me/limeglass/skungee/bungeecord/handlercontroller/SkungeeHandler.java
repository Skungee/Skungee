package me.limeglass.skungee.bungeecord.handlercontroller;

import java.net.InetAddress;

import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;

public abstract class SkungeeHandler {

	private SkungeePacketType[] types = new SkungeePacketType[] {SkungeePacketType.CUSTOM};
	private String name = "";

	/**
	 * Use this when creating custom SkungeeHandlers.
	 * 
	 * @param name The name of this custom SkungeeHandler.
	 */
	public SkungeeHandler(String name) {
		this.name = name;
	}

	/**
	 * The SkungeePacketType is used internally for already created SkungeeHandlers.
	 * 
	 * @param types The SkungeePacketTypes to associate this handler with.
	 */
	public SkungeeHandler(SkungeePacketType... types) {
		this.types = types;
	}

	public SkungeePacketType[] getTypes() {
		return types;
	}

	public String getName() {
		return name;
	}

	/**
	 * Called when the packet handler is requested.
	 * 
	 * @param packet The incoming packet to handle.
	 * @param address The address the packet came from.
	 * @return If the packet should be accepted or not.
	 */
	public abstract boolean onPacketCall(SkungeePacket packet, InetAddress address);

	/**
	 * The main packet handler. This is what is defined to happen when the packet comes in.
	 * 
	 * @param packet The incoming packet to handle.
	 * @param address The address the packet came from.
	 * @return The value to be send back to the sender if it's a returnable packet.
	 */
	public abstract Object handlePacket(SkungeePacket packet, InetAddress address);

}
