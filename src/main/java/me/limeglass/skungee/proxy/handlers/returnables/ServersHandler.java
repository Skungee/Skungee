package me.limeglass.skungee.proxy.handlers.returnables;

import java.net.InetAddress;
import java.util.Set;
import java.util.stream.Collectors;

import me.limeglass.skungee.Skungee;
import me.limeglass.skungee.common.handlercontroller.SkungeeProxyHandler;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.common.wrappers.SkungeePlatform.Platform;

public class ServersHandler extends SkungeeProxyHandler<Set<String>> {

	public ServersHandler() {
		super(Platform.ANY_PROXY, ServerPacketType.ALLSERVERS);
	}

	@Override
	public Set<String> handlePacket(ServerPacket packet, InetAddress address) {
		return Skungee.getServers().keySet().parallelStream().collect(Collectors.toSet());
	}

}
