package me.limeglass.skungee.common.handlercontroller;

import java.net.InetAddress;

import me.limeglass.skungee.Skungee;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.common.wrappers.SkungeePlatform;
import me.limeglass.skungee.common.wrappers.SkungeePlatform.Platform;

public abstract class SkungeeHandler<T> {

	private ServerPacketType[] types = new ServerPacketType[] {ServerPacketType.CUSTOM};
	protected final SkungeePlatform platform = Skungee.getPlatform();
	private final Platform acceptedPlatform;
	private String name = "";

	/**
	 * Use this when creating custom SkungeeHandlers.
	 * 
	 * @param acceptedPlatform The platform this handler can run on.
	 * @param name The name of this custom SkungeeHandler.
	 */
	public SkungeeHandler(Platform acceptedPlatform, String name) {
		this.acceptedPlatform = acceptedPlatform;
		this.name = name;
	}

	/**
	 * The SkungeePacketType is used internally for already created SkungeeHandlers.
	 * 
	 * @param acceptedPlatform The platform this handler can run on.
	 * @param types The SkungeePacketTypes to associate this handler with.
	 */
	public SkungeeHandler(Platform acceptedPlatform, ServerPacketType... types) {
		this.acceptedPlatform = acceptedPlatform;
		this.types = types;
	}

	public boolean acceptsPlatform(Platform platform) {
		if (acceptedPlatform == Platform.ANY_PROXY && platform == Platform.BUNGEECORD || platform == Platform.VELOCITY)
			return true;
		if (acceptedPlatform == platform)
			return true;
		return false;
	}

	public ServerPacketType[] getTypes() {
		return types;
	}

	public Platform getAcceptedPlatform() {
		return acceptedPlatform;
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
	public abstract boolean onPacketCall(ServerPacket packet, ServerPacketType called, InetAddress address);

	/**
	 * The main packet handler. This is what is defined to happen when the packet comes in.
	 * 
	 * @param packet The incoming packet to handle.
	 * @param address The address the packet came from.
	 * @return The value to be send back to the sender if it's a returnable packet.
	 */
	public abstract T handlePacket(ServerPacket packet, InetAddress address);

}
