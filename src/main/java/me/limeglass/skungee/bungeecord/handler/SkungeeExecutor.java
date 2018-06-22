package me.limeglass.skungee.bungeecord.handler;

import java.net.InetAddress;
import me.limeglass.skungee.objects.SkungeePacket;

public abstract class SkungeeExecutor extends SkungeeHandler {
	
	public abstract void executePacket(SkungeePacket packet, InetAddress address);

	@Override
	public Object handlePacket(SkungeePacket packet, InetAddress address) {
		executePacket(packet, address);
		return null;
	}
}