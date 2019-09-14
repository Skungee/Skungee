package me.limeglass.skungee.bungeecord.handlers;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Set;
import java.util.Map.Entry;

import me.limeglass.skungee.bungeecord.Skungee;
import me.limeglass.skungee.bungeecord.handlercontroller.SkungeeBungeeHandler;
import me.limeglass.skungee.bungeecord.sockets.ServerTracker;
import me.limeglass.skungee.objects.ConnectedServer;
import me.limeglass.skungee.objects.SkungeePlayer;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

public class HandshakeHandler extends SkungeeBungeeHandler {

	public HandshakeHandler() {
		super(SkungeePacketType.HANDSHAKE);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object handlePacket(SkungeePacket packet, InetAddress address) {
		if (packet.getObject() == null)
			return null;
		ArrayList<Object> data = (ArrayList<Object>) packet.getObject();
		boolean usingReciever = (boolean) data.get(0);
		int recieverPort = (int) data.get(1);
		int port = (int) data.get(2);
		Set<SkungeePlayer> whitelisted = (Set<SkungeePlayer>) data.get(3);
		int heartbeat = (int) data.get(4);
		String motd = (String) data.get(5);
		int max = (int) data.get(6);
		try {
			for (Entry<String, ServerInfo> server : ProxyServer.getInstance().getServers().entrySet()) {
				InetSocketAddress serverAddress = server.getValue().getAddress();
				// Check the packet provided address first.
				if (serverAddress.getAddress().equals(address) && port == serverAddress.getPort()) {
					if (!ServerTracker.contains(address, port)) {
						ConnectedServer connect = new ConnectedServer(usingReciever, recieverPort, port, address, heartbeat, server.getKey(), motd, max, whitelisted);
						ServerTracker.add(connect);
						ServerTracker.update(serverAddress);
						return "CONNECTED";
					} else {
						ServerTracker.update(serverAddress);
						return "ALREADY";
					}
				}
				// Check all system network interfaces.
				for (Enumeration<NetworkInterface> entry = NetworkInterface.getNetworkInterfaces(); entry.hasMoreElements();) {
					for (Enumeration<InetAddress> addresses = entry.nextElement().getInetAddresses(); addresses.hasMoreElements();) {
						if (addresses.nextElement().equals(serverAddress.getAddress()) && port == serverAddress.getPort()) {
							if (!ServerTracker.contains(address, port)) {
								ConnectedServer connect = new ConnectedServer(usingReciever, recieverPort, port, address, heartbeat, server.getKey(), motd, max, whitelisted);
								ServerTracker.add(connect);
								ServerTracker.update(serverAddress);
								return "CONNECTED";
							} else {
								ServerTracker.update(serverAddress);
								return "ALREADY";
							}
						}
					}
				}
			}
		} catch (SocketException exception) {
			Skungee.exception(exception, "Could not find the system's local host.");
		}
		return null;
	}

}
