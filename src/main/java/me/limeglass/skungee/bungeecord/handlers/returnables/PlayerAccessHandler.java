package me.limeglass.skungee.bungeecord.handlers.returnables;

import java.net.InetAddress;
import me.limeglass.skungee.bungeecord.handlercontroller.SkungeePlayerHandler;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

public class PlayerAccessHandler extends SkungeePlayerHandler {

	static {
		registerHandler(new PlayerAccessHandler(), SkungeePacketType.PLAYERACCESS);
	}

	@Override
	public Object handlePacket(SkungeePacket packet, InetAddress address) {
		if (packet.getObject() == null) return null;
		ServerInfo server = ProxyServer.getInstance().getServerInfo((String) packet.getObject());
		if (server == null) return null;
		//Should only contain one player.
		return players.parallelStream().anyMatch(player -> server.canAccess(player));
	}
	
}