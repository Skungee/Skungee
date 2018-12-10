package me.limeglass.skungee.bungeecord.handlers;

import java.net.InetAddress;
import me.limeglass.skungee.bungeecord.handlercontroller.SkungeePlayerHandler;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerConnectRequest;
import net.md_5.bungee.api.config.ServerInfo;

public class PlayerConnectHandler extends SkungeePlayerHandler {

	static {
		registerHandler(new PlayerConnectHandler(), SkungeePacketType.CONNECTPLAYER);
	}

	@Override
	public Object handlePacket(SkungeePacket packet, InetAddress address) {
		if (packet.getObject() == null)
			return null;
		ServerInfo server = ProxyServer.getInstance().getServerInfo((String) packet.getObject());
		if (server == null)
			return null;
		ServerConnectRequest connection = ServerConnectRequest.builder()
				.target(server)
				.retry(true)
				.build();
		players.forEach(player -> player.connect(connection));
		return null;
	}

}
