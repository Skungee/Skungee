package me.limeglass.skungee.proxy.handlers.returnables;

import java.net.InetAddress;
import java.util.stream.Collectors;

import me.limeglass.skungee.common.handlercontroller.SkungeeBungeePlayerHandler;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.proxy.protocol.ProtocolPlayerManager;

public class PlayerPitchHandler extends SkungeeBungeePlayerHandler {

	public PlayerPitchHandler() {
		super(ServerPacketType.PLAYERPITCH);
	}

	@Override
	public Object handlePacket(ServerPacket packet, InetAddress address) {
		return players.parallelStream()
				.map(player -> ProtocolPlayerManager.getPlayer(player.getUniqueId()))
				.filter(optional -> optional.isPresent())
				.map(optional -> optional.get().getPitch())
				.collect(Collectors.toSet());
	}

}
