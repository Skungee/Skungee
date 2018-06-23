package me.limeglass.skungee.bungeecord.handlers;

import java.net.InetAddress;
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
import me.limeglass.skungee.objects.SkungeePacket;
import me.limeglass.skungee.objects.SkungeePacketType;
import me.limeglass.skungee.objects.SkungeePlayer;
import net.md_5.bungee.api.config.ServerInfo;

public class HandshakeHandler extends SkungeeBungeeHandler {
	
	private static final long serialVersionUID = 6313562508528002437L;

	static {
		registerPacket(new HandshakeHandler(), SkungeePacketType.HANDSHAKE);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object handlePacket(SkungeePacket packet, InetAddress address) {
		if (packet.getObject() == null) return null;
		ArrayList<Object> data = (ArrayList<Object>) packet.getObject();
		Boolean usingReciever = (Boolean) data.get(0);
		Integer recieverPort = (Integer) data.get(1);
		Integer port = (Integer) data.get(2);
		Set<SkungeePlayer> whitelisted = (Set<SkungeePlayer>) data.get(3);
		Integer heartbeat = (Integer) data.get(4);
		String motd = (String) data.get(5);
		Integer max = (Integer) data.get(6);
		try {
			for (Entry<String, ServerInfo> server : servers.entrySet()) {
				String serverAddress = server.getValue().getAddress().getAddress().getHostAddress();
				for (Enumeration<NetworkInterface> entry = NetworkInterface.getNetworkInterfaces(); entry.hasMoreElements();) {
					for (Enumeration<InetAddress> addresses = entry.nextElement().getInetAddresses(); addresses.hasMoreElements();) {
						if (addresses.nextElement().getHostAddress().equals(serverAddress) && port == server.getValue().getAddress().getPort()) {
							ConnectedServer connect = new ConnectedServer(usingReciever, recieverPort, port, address, heartbeat, server.getKey(), motd, max, whitelisted);
							if (!ServerTracker.contains(connect)) {
								ServerTracker.add(connect);
								ServerTracker.update(server.getKey());
								return "CONNECTED";
							}
						}
					}
				}
				if (serverAddress.equals(address.getHostAddress()) && port == server.getValue().getAddress().getPort()) {
					ConnectedServer connect = new ConnectedServer(usingReciever, recieverPort, port, address, heartbeat, server.getKey(), motd, max, whitelisted);
					if (!ServerTracker.contains(connect)) {
						ServerTracker.add(connect);
						ServerTracker.update(server.getKey());
						return "CONNECTED";
					}
				}
			}
		} catch (SocketException exception) {
			Skungee.exception(exception, "Could not find the system's local host.");
		}
		return null;
	}
}
