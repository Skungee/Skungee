package me.limeglass.skungee.bungeecord.handlercontroller;

import java.net.InetAddress;

import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;

public abstract class SkungeeExecutor extends SkungeeHandler {

	public SkungeeExecutor(String name) {
		super(name);
	}

	public SkungeeExecutor(SkungeePacketType... types) {
		super(types);
	}

	public abstract void executePacket(SkungeePacket packet, InetAddress address);

	@Override
	public Object handlePacket(SkungeePacket packet, InetAddress address) {
		executePacket(packet, address);
		return null;
	}

	@Override
	public boolean onPacketCall(SkungeePacket packet, InetAddress address) {
		return true;
	}

}
