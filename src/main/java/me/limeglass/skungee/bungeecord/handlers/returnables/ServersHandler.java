package me.limeglass.skungee.bungeecord.handlers.returnables;

import java.net.InetAddress;
import java.util.stream.Collectors;

import me.limeglass.skungee.bungeecord.handlercontroller.SkungeeBungeeHandler;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;

public class ServersHandler extends SkungeeBungeeHandler {

	static {
		registerHandler(new PlayerViewDistanceHandler(), SkungeePacketType.ALLSERVERS);
	}
	
	@Override
	public Object handlePacket(SkungeePacket packet, InetAddress address) {
		return servers.keySet().parallelStream().collect(Collectors.toSet());
	}

}
