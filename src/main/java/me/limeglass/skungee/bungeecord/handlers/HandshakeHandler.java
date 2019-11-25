package me.limeglass.skungee.bungeecord.handlers;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Map.Entry;
import java.util.Set;

import me.limeglass.skungee.bungeecord.Skungee;
import me.limeglass.skungee.bungeecord.handlercontroller.SkungeeBungeeHandler;
import me.limeglass.skungee.bungeecord.sockets.ServerTracker;
import me.limeglass.skungee.objects.ConnectedServer;
import me.limeglass.skungee.objects.SkungeePlayer;
import me.limeglass.skungee.objects.packets.HandshakePacket;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

public class HandshakeHandler extends SkungeeBungeeHandler {

	public HandshakeHandler() {
		super(SkungeePacketType.HANDSHAKE);
	}

	@Override
	public Object handlePacket(SkungeePacket packet, InetAddress address) {
		if (!(packet instanceof HandshakePacket))
			return null;
		HandshakePacket handshake = (HandshakePacket) packet;
		Set<SkungeePlayer> whitelisted = handshake.getWhitelisted();
		boolean hasReciever = handshake.hasReciever();
		int recieverPort = handshake.getRecieverPort();
		int heartbeat = handshake.getHeartbeat();
		int max = handshake.getMaximumPlayers();
		String motd = handshake.getMotd();
		int port = handshake.getPort();
		try {
			for (Entry<String, ServerInfo> server : ProxyServer.getInstance().getServers().entrySet()) {
				InetSocketAddress serverAddress = server.getValue().getAddress();
				// Check the packet provided address first.
				if (serverAddress.getAddress().equals(address) && port == serverAddress.getPort()) {
					if (!ServerTracker.contains(address, port)) {
						ConnectedServer connect = new ConnectedServer(hasReciever, recieverPort, port, address, heartbeat, server.getKey(), motd, max, whitelisted);
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
								ConnectedServer connect = new ConnectedServer(hasReciever, recieverPort, port, address, heartbeat, server.getKey(), motd, max, whitelisted);
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
