package me.limeglass.skungee.proxy.handlers;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Optional;

import me.limeglass.skungee.common.handlercontroller.SkungeeExecutor;
import me.limeglass.skungee.common.objects.SkungeeServer;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.common.wrappers.ProxyPlatform;
import me.limeglass.skungee.common.wrappers.SkungeePlatform.Platform;
import me.limeglass.skungee.proxy.sockets.ServerTracker;

public class DisconnectHandler extends SkungeeExecutor {

	public DisconnectHandler() {
		super(Platform.ANY_PROXY, ServerPacketType.DISCONNECT);
	}

	@Override
	public void executePacket(ServerPacket packet, InetAddress address) {
		if (packet.getObject() == null)
			return;
		ServerTracker tracker = ((ProxyPlatform) platform).getServerTracker();
		Optional<SkungeeServer> server = tracker.getByAddress(new InetSocketAddress(address, (int)packet.getObject()));
		if (!server.isPresent())
			return;
		tracker.notResponding(server.get());
	}

}
