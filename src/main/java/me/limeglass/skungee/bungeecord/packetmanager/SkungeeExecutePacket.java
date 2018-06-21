package me.limeglass.skungee.bungeecord.packetmanager;

import java.net.InetAddress;
import me.limeglass.skungee.objects.SkungeePacket;

public abstract class SkungeeExecutePacket extends SkungeeHandlerPacket {
	
	public abstract void executePacket(SkungeePacket packet, InetAddress address);

	@Override
	public Object handlePacket(SkungeePacket packet, InetAddress address) {
		executePacket(packet, address);
		return null;
	}
}