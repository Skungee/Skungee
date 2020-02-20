package me.limeglass.skungee.proxy.handlers.returnables;

import java.net.InetAddress;

import me.limeglass.skungee.bungeecord.BungeePlayer;
import me.limeglass.skungee.common.handlercontroller.SkungeeProxyHandler;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.common.wrappers.SkungeePlatform.Platform;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

public class PlayerAccessHandler extends SkungeeProxyHandler<Boolean> {

	public PlayerAccessHandler() {
		super(Platform.BUNGEECORD, ServerPacketType.PLAYERACCESS);
	}

	@Override
	public Boolean handlePacket(ServerPacket packet, InetAddress address) {
		if (packet.getObject() == null)
			return null;
		ServerInfo server = ProxyServer.getInstance().getServerInfo((String) packet.getObject());
		if (server == null)
			return null;
		// Only one player gets past in this, fix it.
		return proxy.getPlayers(packet.getPlayers()).stream()
				.map(player -> ((BungeePlayer)player).getPlayer())
				.filter(optional -> optional.isPresent())
				.map(optional -> optional.get())
				.anyMatch(player -> server.canAccess(player));
	}

}
