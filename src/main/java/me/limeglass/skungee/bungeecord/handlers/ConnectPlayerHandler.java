package me.limeglass.skungee.bungeecord.handlers;

import java.net.InetAddress;
import me.limeglass.skungee.bungeecord.handlercontroller.SkungeePlayerHandler;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ConnectPlayerHandler extends SkungeePlayerHandler {

	static {
		registerHandler(new ConnectPlayerHandler(), SkungeePacketType.CONNECTPLAYER);
	}

	@Override
	public Object handlePacket(SkungeePacket packet, InetAddress address) {
		if (packet.getObject() == null) return null;
		for (ProxiedPlayer player : players) {
			ServerInfo serverinfo = ProxyServer.getInstance().getServerInfo((String) packet.getObject());
			if (serverinfo != null) player.connect(serverinfo);
		}
		return null;
	}

}
