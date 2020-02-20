package me.limeglass.skungee.proxy.handlers.returnables;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

import me.limeglass.skungee.common.handlercontroller.SkungeeProxyHandler;
import me.limeglass.skungee.common.objects.SkungeeServer;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.common.player.PacketPlayer;
import me.limeglass.skungee.common.wrappers.SkungeePlatform.Platform;
import me.limeglass.skungee.proxy.sockets.ServerTracker;

public class ServersWhitelistedHandler extends SkungeeProxyHandler<Set<PacketPlayer>> {

	public ServersWhitelistedHandler() {
		super(Platform.ANY_PROXY, ServerPacketType.WHITELISTED);
	}

	@Override
	public Set<PacketPlayer> handlePacket(ServerPacket packet, InetAddress address) {
		Set<PacketPlayer> whitelisted = new HashSet<>();
		if (packet.getObject() == null)
			return whitelisted;
		ServerTracker tracker = proxy.getServerTracker();
		for (String server : (String[]) packet.getObject()) {
			for (SkungeeServer tracked : tracker.get(server)) {
				if (tracked == null || !tracker.isResponding(tracked))
					continue;
				whitelisted.addAll(tracked.getWhitelisted());
			}
		}
		return whitelisted;
	}

}
