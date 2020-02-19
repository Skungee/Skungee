package me.limeglass.skungee.proxy.handlers.returnables;

import java.net.InetAddress;
import java.util.stream.Collectors;

import me.limeglass.skungee.common.handlercontroller.SkungeeBungeePlayerHandler;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;

public class PlayerServerHandler extends SkungeeBungeePlayerHandler {

	public PlayerServerHandler() {
		super(ServerPacketType.PLAYERSERVER);
	}

	@Override
	public Object handlePacket(ServerPacket packet, InetAddress address) {
		if (players == null || players.isEmpty())
			return null;
		return players.parallelStream()
				.map(player -> player.getServer().getInfo().getName())
				.collect(Collectors.toSet());
	}

}
