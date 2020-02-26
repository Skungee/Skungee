package me.limeglass.skungee.proxy.handlers.returnables;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Optional;

import me.limeglass.skungee.common.handlercontroller.SkungeeProxyHandler;
import me.limeglass.skungee.common.objects.SkungeeServer;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.common.wrappers.SkungeePlatform.Platform;
import me.limeglass.skungee.proxy.sockets.ServerTracker;

public class CurrentServerHandler extends SkungeeProxyHandler<SkungeeServer> {

	public CurrentServerHandler() {
		super(Platform.ANY_PROXY, ServerPacketType.CURRENTSERVER);
	}

	@Override
	public SkungeeServer handlePacket(ServerPacket packet, InetAddress address) {
		ServerTracker tracker = proxy.getServerTracker();
		InetSocketAddress a = new InetSocketAddress(address, (int)packet.getObject());
		Optional<SkungeeServer> server = tracker.getByAddress(a);
		if (!server.isPresent())
			return null;
		return server.get();
	}

}
