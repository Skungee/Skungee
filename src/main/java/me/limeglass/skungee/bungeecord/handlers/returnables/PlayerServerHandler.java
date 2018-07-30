package me.limeglass.skungee.bungeecord.handlers.returnables;

import java.net.InetAddress;
import java.util.stream.Collectors;

import me.limeglass.skungee.bungeecord.handlercontroller.SkungeePlayerHandler;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;

public class PlayerServerHandler extends SkungeePlayerHandler {

	static {
		registerHandler(new PlayerServerHandler(), SkungeePacketType.PLAYERSERVER);
	}

	@Override
	public Object handlePacket(SkungeePacket packet, InetAddress address) {
		if (packet.getObject() == null) return null;
		return players.parallelStream().map(player -> player.getServer().getInfo().getName()).collect(Collectors.toSet());
	}
	
}