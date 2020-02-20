package me.limeglass.skungee.proxy.handlers.returnables;

import java.net.InetAddress;
import java.util.Set;
import java.util.stream.Collectors;

import me.limeglass.skungee.common.handlercontroller.SkungeeProxyHandler;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.common.player.PacketPlayer;
import me.limeglass.skungee.common.wrappers.SkungeePlatform.Platform;

public class PlayersHandler extends SkungeeProxyHandler<Set<PacketPlayer>> {

	public PlayersHandler() {
		super(Platform.ANY_PROXY, ServerPacketType.GLOBALPLAYERS);
	}

	@Override
	public Set<PacketPlayer> handlePacket(ServerPacket packet, InetAddress address) {
		return proxy.getPlayers().stream()
				.map(player -> new PacketPlayer(player.getUUID(), player.getUsername()))
				.collect(Collectors.toSet());
	}

}
