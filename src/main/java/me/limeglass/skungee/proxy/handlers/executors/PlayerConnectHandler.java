package me.limeglass.skungee.proxy.handlers.executors;

import java.net.InetAddress;

import me.limeglass.skungee.common.handlercontroller.SkungeeExecutor;
import me.limeglass.skungee.common.objects.SkungeeServer;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.common.wrappers.ProxyPlatform;
import me.limeglass.skungee.common.wrappers.SkungeePlatform.Platform;

public class PlayerConnectHandler extends SkungeeExecutor {

	public PlayerConnectHandler() {
		super(Platform.ANY_PROXY, ServerPacketType.CONNECTPLAYER);
	}

	@Override
	public void executePacket(ServerPacket packet, InetAddress address) {
		if (packet.getObject() == null)
			return;
		ProxyPlatform proxy = (ProxyPlatform) platform;
		SkungeeServer[] servers = proxy.getServerTracker().get((String) packet.getObject());
		if (servers == null || servers.length <= 0)
			return;
		proxy.connect(servers[0], proxy.getPlayers(packet.getPlayers()));
	}

}
