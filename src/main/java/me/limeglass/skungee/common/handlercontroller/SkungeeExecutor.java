package me.limeglass.skungee.common.handlercontroller;

import java.net.InetAddress;

import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.common.wrappers.SkungeePlatform.Platform;

public abstract class SkungeeExecutor extends SkungeeHandler<Object> {

	public SkungeeExecutor(Platform platform, String name) {
		super(platform, name);
	}

	public SkungeeExecutor(Platform platform, ServerPacketType... types) {
		super(platform, types);
	}

	public abstract void executePacket(ServerPacket packet, InetAddress address);

	@Override
	public Object handlePacket(ServerPacket packet, InetAddress address) {
		executePacket(packet, address);
		return null;
	}

	@Override
	public boolean onPacketCall(ServerPacket packet, ServerPacketType called, InetAddress address) {
		return true;
	}

}
