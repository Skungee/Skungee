package me.limeglass.skungee.proxy.handlers.returnables;

import java.net.InetAddress;

import me.limeglass.skungee.common.handlercontroller.SkungeeBungeePlayerHandler;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;

public class PlayerLegacyHandler extends SkungeeBungeePlayerHandler {

	public PlayerLegacyHandler() {
		super(ServerPacketType.PLAYERLEGACY);
	}

	@Override
	public Object handlePacket(ServerPacket packet, InetAddress address) {
		// Only one player gets past in this, fix it.
		return players.parallelStream().anyMatch(player -> player.getPendingConnection().isLegacy());
	}

}
