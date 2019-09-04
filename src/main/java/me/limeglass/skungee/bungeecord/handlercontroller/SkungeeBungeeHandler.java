package me.limeglass.skungee.bungeecord.handlercontroller;

import java.net.InetAddress;

import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;

public abstract class SkungeeBungeeHandler extends SkungeeHandler {

	public SkungeeBungeeHandler(String name) {
		super(name);
	}
	
	public SkungeeBungeeHandler(SkungeePacketType... types) {
		super(types);
	}

	@Override
	public boolean onPacketCall(SkungeePacket packet, InetAddress address) {
		return true;
	}

}
