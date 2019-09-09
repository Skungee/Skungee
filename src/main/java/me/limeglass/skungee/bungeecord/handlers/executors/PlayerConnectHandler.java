package me.limeglass.skungee.bungeecord.handlers.executors;

import java.net.InetAddress;
import me.limeglass.skungee.bungeecord.handlercontroller.SkungeePlayerHandler;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerConnectRequest;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerConnectEvent.Reason;

public class PlayerConnectHandler extends SkungeePlayerHandler {

	public PlayerConnectHandler() {
		super(SkungeePacketType.CONNECTPLAYER);
	}

	@Override
	public Object handlePacket(SkungeePacket packet, InetAddress address) {
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
