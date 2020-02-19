package me.limeglass.skungee.proxy.handlers.returnables;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

import me.limeglass.skungee.common.handlercontroller.SkungeeProxyHandler;
import me.limeglass.skungee.common.objects.SkungeeServer;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.common.wrappers.SkungeePlatform.Platform;
import me.limeglass.skungee.proxy.sockets.ServerTracker;

public class ServersMOTDHandler extends SkungeeProxyHandler {

	public ServersMOTDHandler() {
		super(Platform.ANY_PROXY, ServerPacketType.SERVERMOTD);
	}

	@Override
	public Object handlePacket(ServerPacket packet, InetAddress address) {
		Set<String> motds = new HashSet<>();
		if (packet.getObject() == null)
			return motds;
		for (String server : (String[]) packet.getObject()) {
			for (SkungeeServer tracked : ServerTracker.get(server)) {
				if (tracked == null || !ServerTracker.isResponding(tracked))
					continue;
				String motd = tracked.getMotd();
				if (motd != null)
					motds.add(motd);
			}
		}
		return motds;
	}

}
