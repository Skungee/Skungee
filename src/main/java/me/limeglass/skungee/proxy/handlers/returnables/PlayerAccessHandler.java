package me.limeglass.skungee.proxy.handlers.returnables;

import java.net.InetAddress;

import me.limeglass.skungee.common.handlercontroller.SkungeeBungeePlayerHandler;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

public class PlayerAccessHandler extends SkungeeBungeePlayerHandler {

	public PlayerAccessHandler() {
		super(ServerPacketType.PLAYERACCESS);
	}

	@Override
	public Object handlePacket(ServerPacket packet, InetAddress address) {
		if (packet.getObject() == null)
			return null;
		ServerInfo server = ProxyServer.getInstance().getServerInfo((String) packet.getObject());
		if (server == null)
			return null;
		// Only one player gets past in this, fix it.
		return players.stream().anyMatch(player -> server.canAccess(player));
	}

}
