package me.limeglass.skungee.proxy.handlers.returnables;

import java.net.InetAddress;
import java.util.Set;
import java.util.stream.Collectors;

import me.limeglass.skungee.common.handlercontroller.SkungeeProxyHandler;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.common.wrappers.SkungeePlatform.Platform;

public class PlayerNameHandler extends SkungeeProxyHandler<Set<String>> {

	public PlayerNameHandler() {
		super(Platform.ANY_PROXY, ServerPacketType.PLAYERNAME);
	}

	@Override
	public Set<String> handlePacket(ServerPacket packet, InetAddress address) {
		return proxy.getPlayers(packet.getPlayers()).stream()
				.map(player -> player.getUsername())
				.collect(Collectors.toSet());
	}

}
