package me.limeglass.skungee.bungeecord.handlers.returnables;

import java.net.InetAddress;
import java.util.stream.Collectors;

import me.limeglass.skungee.bungeecord.handlercontroller.SkungeeBungeeHandler;
import me.limeglass.skungee.objects.SkungeePlayer;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import net.md_5.bungee.api.ProxyServer;

public class PlayersHandler extends SkungeeBungeeHandler {

	static {
		registerHandler(new PlayersHandler(), SkungeePacketType.GLOBALPLAYERS);
	}
	
	@Override
	public Object handlePacket(SkungeePacket packet, InetAddress address) {
		return ProxyServer.getInstance().getPlayers().parallelStream()
				.map(player -> new SkungeePlayer(false, player.getUniqueId(), player.getName()))
				.collect(Collectors.toSet());
	}

}
