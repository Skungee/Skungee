package me.limeglass.skungee.bungeecord.handlers;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map.Entry;

import me.limeglass.skungee.bungeecord.Skungee;
import me.limeglass.skungee.bungeecord.handlercontroller.SkungeeBungeeHandler;
import me.limeglass.skungee.bungeecord.sockets.ServerTracker;
import me.limeglass.skungee.objects.SkungeePacket;
import me.limeglass.skungee.objects.SkungeePacketType;
import net.md_5.bungee.api.config.ServerInfo;

public class HeartbeatHandler extends SkungeeBungeeHandler {

	static {
		registerPacket(new HeartbeatHandler(), SkungeePacketType.HEARTBEAT);
	}

	@Override
	public Object handlePacket(SkungeePacket packet, InetAddress address) {
		if (packet.getObject() == null) return null;
		int port = (int) packet.getObject();
		for (Entry<String, ServerInfo> server : servers.entrySet()) {
			InetSocketAddress inetaddress = new InetSocketAddress(address, port);
			try {
				if (server.getValue().getAddress().equals(inetaddress) || Inet4Address.getLocalHost().getHostAddress().equals(address.getHostAddress())) {
					return ServerTracker.update(server.getKey());
				}
			} catch (UnknownHostException error) {
				Skungee.exception(error, "Unknown host: " + inetaddress);
			}
		}
		return null;
	}
}
