package me.limeglass.skungee.bungeecord.handlers;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Map.Entry;

import me.limeglass.skungee.bungeecord.Skungee;
import me.limeglass.skungee.bungeecord.handlercontroller.SkungeeBungeeHandler;
import me.limeglass.skungee.bungeecord.sockets.ServerTracker;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

public class HeartbeatHandler extends SkungeeBungeeHandler {

	public HeartbeatHandler() {
		super(SkungeePacketType.HEARTBEAT);
	}

	@SuppressWarnings("deprecation")
	@Override
	public Object handlePacket(SkungeePacket packet, InetAddress address) {
		if (packet.getObject() == null)
			return null;
		int port = (int) packet.getObject();
		for (Entry<String, ServerInfo> server : ProxyServer.getInstance().getServers().entrySet()) {
			InetSocketAddress inetaddress = new InetSocketAddress(address, port);
			try {
				if (server.getValue().getAddress().equals(inetaddress) || address.isAnyLocalAddress() || address.isLoopbackAddress()) {
					return ServerTracker.update(inetaddress);
				// Last hope checks.
				} else if (NetworkInterface.getByInetAddress(address) != null) {
					return ServerTracker.update(inetaddress);
				} else if (Inet4Address.getLocalHost().getHostAddress().equals(address.getHostAddress())) {
					return ServerTracker.update(inetaddress);
				}
			} catch (SocketException socket) {
				Skungee.exception(socket, "Socket unknown host: " + inetaddress);
			} catch (UnknownHostException host) {
				Skungee.exception(host, "Unknown host: " + inetaddress);
			}
		}
		return null;
	}

//	@Override
//	public Object handlePacket(SkungeePacket packet, InetAddress address) {
//		if (packet.getObject() == null)
//			return null;
//		int port = (int) packet.getObject();
//		InetSocketAddress inetaddress = new InetSocketAddress(address, port);
//		for (Entry<String, ServerInfo> server : ProxyServer.getInstance().getServers().entrySet()) {
//			if (server.getValue().getAddress().equals(inetaddress))
//				return ServerTracker.update(server.getKey());
//			try {
//				if (Inet4Address.getLocalHost().equals(address))
//					return ServerTracker.update(server.getKey());
//			} catch (UnknownHostException e) {
//				Skungee.exception(e, "Could not find localhost");
//			}
//		}
//		return null;
//	}

}
