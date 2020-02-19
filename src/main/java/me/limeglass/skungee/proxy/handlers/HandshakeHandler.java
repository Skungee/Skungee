package me.limeglass.skungee.proxy.handlers;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Map.Entry;
import java.util.Set;

import me.limeglass.skungee.Skungee;
import me.limeglass.skungee.common.handlercontroller.SkungeeProxyHandler;
import me.limeglass.skungee.common.objects.SkungeeServer;
import me.limeglass.skungee.common.packets.HandshakePacket;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.common.player.PacketPlayer;
import me.limeglass.skungee.common.wrappers.SkungeePlatform.Platform;
import me.limeglass.skungee.proxy.sockets.ServerTracker;

public class HandshakeHandler extends SkungeeProxyHandler {

	public HandshakeHandler() {
		super(Platform.ANY_PROXY, ServerPacketType.HANDSHAKE);
	}

	@Override
	public Object handlePacket(ServerPacket packet, InetAddress address) {
		if (!(packet instanceof HandshakePacket))
			return null;
		HandshakePacket handshake = (HandshakePacket) packet;
		Set<PacketPlayer> whitelisted = handshake.getWhitelisted();
		boolean hasReciever = handshake.hasReciever();
		int recieverPort = handshake.getRecieverPort();
		int heartbeat = handshake.getHeartbeat();
		int max = handshake.getMaximumPlayers();
		String motd = handshake.getMotd();
		int port = handshake.getPort();
		ServerTracker tracker = proxy.getServerTracker();
		try {
			for (Entry<String, InetSocketAddress> server : Skungee.getServers().entrySet()) {
				// Check the packet provided address first.
				if (server.getValue().getAddress().equals(address) && port == server.getValue().getPort()) {
					if (!tracker.contains(new InetSocketAddress(address, port))) {
						SkungeeServer connect = new SkungeeServer(hasReciever, recieverPort, port, address, heartbeat, server.getKey(), motd, max, whitelisted);
						tracker.add(connect);
						tracker.update(server.getValue());
						return "CONNECTED";
					} else {
						tracker.update(server.getValue());
						return "ALREADY";
					}
				}
				// Check all system network interfaces.
				for (Enumeration<NetworkInterface> entry = NetworkInterface.getNetworkInterfaces(); entry.hasMoreElements();) {
					for (Enumeration<InetAddress> addresses = entry.nextElement().getInetAddresses(); addresses.hasMoreElements();) {
						if (addresses.nextElement().equals(server.getValue().getAddress()) && port == server.getValue().getPort()) {
							if (!tracker.contains(new InetSocketAddress(address, port))) {
								SkungeeServer connect = new SkungeeServer(hasReciever, recieverPort, port, address, heartbeat, server.getKey(), motd, max, whitelisted);
								tracker.add(connect);
								tracker.update(server.getValue());
								return "CONNECTED";
							} else {
								tracker.update(server.getValue());
								return "ALREADY";
							}
						}
					}
				}
			}
		} catch (SocketException exception) {
			platform.exception(exception, "Could not find the system's local host.");
		}
		return null;
	}

}
