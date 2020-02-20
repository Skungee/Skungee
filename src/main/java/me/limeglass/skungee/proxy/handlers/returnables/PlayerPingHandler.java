package me.limeglass.skungee.proxy.handlers.returnables;

import java.net.InetAddress;
import java.util.Set;
import java.util.stream.Collectors;

import me.limeglass.skungee.common.handlercontroller.SkungeeProxyHandler;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.common.wrappers.SkungeePlatform.Platform;

public class PlayerPingHandler extends SkungeeProxyHandler<Set<Number>> {

	public PlayerPingHandler() {
		super(Platform.ANY_PROXY, ServerPacketType.PLAYERPING);
	}

	@Override
	public Set<Number> handlePacket(ServerPacket packet, InetAddress address) {
		return proxy.getPlayers(packet.getPlayers()).stream()
				.map(player -> player.getPing())
				.collect(Collectors.toSet());
	}

}
