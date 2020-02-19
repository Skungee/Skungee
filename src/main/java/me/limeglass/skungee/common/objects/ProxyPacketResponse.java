package me.limeglass.skungee.common.objects;

import me.limeglass.skungee.common.packets.ProxyPacket;
import me.limeglass.skungee.common.wrappers.PacketResponse;
import me.limeglass.skungee.common.wrappers.SkungeePlatform.Platform;

public class ProxyPacketResponse implements PacketResponse {

	private final SkungeeServer server;
	private final ProxyPacket packet;
	private final Platform platform;
	private final Object object;

	public ProxyPacketResponse(Platform platform, SkungeeServer server, ProxyPacket packet, Object object) {
		this.platform = platform;
		this.packet = packet;
		this.server = server;
		this.object = object;
	}

	public SkungeeServer getReceivingServer() {
		return server;
	}

	@Override
	public Platform getSendingPlatform() {
		return platform;
	}

	public ProxyPacket getSentPacket() {
		return packet;
	}

	@Override
	public Object getObject() {
		return object;
	}

}
