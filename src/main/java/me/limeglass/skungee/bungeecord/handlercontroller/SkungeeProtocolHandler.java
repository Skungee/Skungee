package me.limeglass.skungee.bungeecord.handlercontroller;

import java.net.InetAddress;

import me.limeglass.skungee.objects.packets.ProtocolPacket;
import me.limeglass.skungee.objects.packets.SkungeePacket;

public abstract class SkungeeProtocolHandler extends SkungeeHandler {
	
	public Object handlePacket(SkungeePacket packet, InetAddress address) {
		if (!ProtocolPacket.class.isAssignableFrom(packet.getClass())) return null;
		return handlePacket((ProtocolPacket)packet);
	}
	
	public abstract boolean handlePacket(ProtocolPacket packet);
	
	@Override
	public Boolean onPacketCall(SkungeePacket packet, InetAddress address) {return true;}
}