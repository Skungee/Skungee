package me.limeglass.skungee.bungeecord.handlers.returnables;

import java.net.InetAddress;
import me.limeglass.skungee.bungeecord.handlercontroller.SkungeePlayerHandler;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;

public class PlayerLegacyHandler extends SkungeePlayerHandler {

	public PlayerLegacyHandler() {
		super(SkungeePacketType.PLAYERLEGACY);
	}

	@Override
	public Object handlePacket(SkungeePacket packet, InetAddress address) {
		// Only one player gets past in this, fix it.
		return players.parallelStream().anyMatch(player -> player.getPendingConnection().isLegacy());
	}

}
