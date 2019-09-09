package me.limeglass.skungee.bungeecord.handlers.returnables;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

import me.limeglass.skungee.bungeecord.handlercontroller.SkungeeBungeeHandler;
import me.limeglass.skungee.bungeecord.sockets.ServerTracker;
import me.limeglass.skungee.objects.ConnectedServer;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;

public class ServersMaxPlayersHandler extends SkungeeBungeeHandler {

	public ServersMaxPlayersHandler() {
		super(SkungeePacketType.MAXPLAYERS);
	}

	@Override
	public Object handlePacket(SkungeePacket packet, InetAddress address) {
		Set<Number> limits = new HashSet<>();
		if (packet.getObject() == null)
			return limits;
		for (String server : (String[]) packet.getObject()) {
			for (ConnectedServer tracked : ServerTracker.get(server)) {
				if (tracked == null || !ServerTracker.isResponding(tracked))
					continue;
				limits.add(tracked.getMaxPlayers());
			}
		}
		return limits;
	}

}
