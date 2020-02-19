package me.limeglass.skungee.proxy.handlers.returnables;

import java.net.InetAddress;
import java.util.stream.Collectors;

import me.limeglass.skungee.common.handlercontroller.SkungeeProxyHandler;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.common.player.SkungeePlayer;
import net.md_5.bungee.api.ProxyServer;

public class PlayersHandler extends SkungeeProxyHandler {

	public PlayersHandler() {
		super(ServerPacketType.GLOBALPLAYERS);
	}
	
	@Override
	public Object handlePacket(ServerPacket packet, InetAddress address) {
		return ProxyServer.getInstance().getPlayers().parallelStream()
				.map(player -> new SkungeePlayer(false, player.getUniqueId(), player.getName()))
				.collect(Collectors.toSet());
	}

}
