package me.limeglass.skungee.bungeecord.handlers;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
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

	@Override
	public Object handlePacket(SkungeePacket packet, InetAddress address) {
		if (packet.getObject() == null)
			return null;
		int port = (int) packet.getObject();
		InetSocketAddress inetaddress = new InetSocketAddress(address, port);
		for (Entry<String, ServerInfo> server : ProxyServer.getInstance().getServers().entrySet()) {
			if (!server.getValue().getAddress().equals(inetaddress))
				continue;
			try {
				if (!Inet4Address.getLocalHost().equals(address))
					continue;
			} catch (UnknownHostException e) {
				Skungee.exception(e, "Could not find localhost");
			}
			return ServerTracker.update(server.getKey());
		}
		return null;
	}

}
