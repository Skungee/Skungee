package me.limeglass.skungee.bungeecord.handlers.returnables;

import java.net.InetAddress;
import java.util.stream.Collectors;

import me.limeglass.skungee.bungeecord.handlercontroller.SkungeeBungeeHandler;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import net.md_5.bungee.api.ProxyServer;

public class ServersHandler extends SkungeeBungeeHandler {

	public ServersHandler() {
		super(SkungeePacketType.ALLSERVERS);
	}

	@Override
	public Object handlePacket(SkungeePacket packet, InetAddress address) {
		return ProxyServer.getInstance().getServers().keySet().parallelStream().collect(Collectors.toSet());
	}

}
