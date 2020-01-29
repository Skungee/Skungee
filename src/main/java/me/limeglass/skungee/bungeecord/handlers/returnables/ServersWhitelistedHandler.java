package me.limeglass.skungee.bungeecord.handlers.returnables;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

import me.limeglass.skungee.bungeecord.handlercontroller.SkungeeBungeeHandler;
import me.limeglass.skungee.bungeecord.sockets.ServerTracker;
import me.limeglass.skungee.objects.ConnectedServer;
import me.limeglass.skungee.objects.SkungeePlayer;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;

public class ServersWhitelistedHandler extends SkungeeBungeeHandler {

	public ServersWhitelistedHandler() {
		super(SkungeePacketType.WHITELISTED);
	}

	@Override
	public Object handlePacket(SkungeePacket packet, InetAddress address) {
		Set<SkungeePlayer> whitelisted = new HashSet<>();
		if (packet.getObject() == null)
			return whitelisted;
		for (String server : (String[]) packet.getObject()) {
			for (ConnectedServer tracked : ServerTracker.get(server)) {
				if (tracked == null || !ServerTracker.isResponding(tracked))
					continue;
				whitelisted.addAll(tracked.getWhitelisted());
			}
		}
		return whitelisted;
	}

}
