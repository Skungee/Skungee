package me.limeglass.skungee.proxy.handlers.returnables;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

import me.limeglass.skungee.common.handlercontroller.SkungeeProxyHandler;
import me.limeglass.skungee.common.objects.SkungeeServer;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.common.player.SkungeePlayer;
import me.limeglass.skungee.common.wrappers.SkungeePlatform.Platform;
import me.limeglass.skungee.proxy.sockets.ServerTracker;

public class ServersWhitelistedHandler extends SkungeeProxyHandler {

	public ServersWhitelistedHandler() {
		super(Platform.ANY_PROXY, ServerPacketType.WHITELISTED);
	}

	@Override
	public Object handlePacket(ServerPacket packet, InetAddress address) {
		Set<SkungeePlayer> whitelisted = new HashSet<>();
		if (packet.getObject() == null)
			return whitelisted;
		for (String server : (String[]) packet.getObject()) {
			for (SkungeeServer tracked : ServerTracker.get(server)) {
				if (tracked == null || !ServerTracker.isResponding(tracked))
					continue;
				whitelisted.addAll(tracked.getWhitelisted());
			}
		}
		return whitelisted;
	}

}
