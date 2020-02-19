package me.limeglass.skungee.proxy.handlers.executors;

import java.net.InetAddress;

import me.limeglass.skungee.common.handlercontroller.SkungeeBungeePlayerHandler;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerConnectRequest;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerConnectEvent.Reason;

public class PlayerConnectHandler extends SkungeeBungeePlayerHandler {

	public PlayerConnectHandler() {
		super(ServerPacketType.CONNECTPLAYER);
	}

	@Override
	public Object handlePacket(ServerPacket packet, InetAddress address) {
		if (packet.getObject() == null)
			return null;
		ServerInfo server = ProxyServer.getInstance().getServerInfo((String) packet.getObject());
		if (server == null)
			return null;
		Reason reason = Reason.PLUGIN;
		if (packet.getSetObject() != null)
			reason = Reason.valueOf((String) packet.getSetObject());
		ServerConnectRequest connection = ServerConnectRequest.builder()
				.reason(reason)
				.target(server)
				.retry(true)
				.build();
		players.forEach(player -> player.connect(connection));
		return null;
	}

}
