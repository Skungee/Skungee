package me.limeglass.skungee.proxy.handlers;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Map.Entry;

import me.limeglass.skungee.Skungee;
import me.limeglass.skungee.common.handlercontroller.SkungeeProxyHandler;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.common.wrappers.SkungeePlatform.Platform;
import me.limeglass.skungee.proxy.sockets.ServerTracker;

public class HeartbeatHandler extends SkungeeProxyHandler<Boolean> {

	public HeartbeatHandler() {
		super(Platform.ANY_PROXY, ServerPacketType.HEARTBEAT);
	}

	@Override
	public Boolean handlePacket(ServerPacket packet, InetAddress address) {
		if (packet.getObject() == null)
			return null;
		int port = (int) packet.getObject();
		ServerTracker tracker = proxy.getServerTracker();
		for (Entry<String, InetSocketAddress> server : Skungee.getServers().entrySet()) {
			InetSocketAddress inetaddress = new InetSocketAddress(address, port);
			try {
				if (server.getValue().equals(inetaddress) || address.isAnyLocalAddress() || address.isLoopbackAddress()) {
					return tracker.update(inetaddress);
				// Last hope checks.
				} else if (NetworkInterface.getByInetAddress(address) != null) {
					return tracker.update(inetaddress);
				} else if (Inet4Address.getLocalHost().getHostAddress().equals(address.getHostAddress())) {
					return tracker.update(inetaddress);
				}
			} catch (SocketException socket) {
				platform.exception(socket, "Socket unknown host: " + inetaddress);
			} catch (UnknownHostException host) {
				platform.exception(host, "Unknown host: " + inetaddress);
			}
		}
		return null;
	}

}
