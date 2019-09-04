package me.limeglass.skungee.bungeecord.handlers.returnables;

import java.net.InetAddress;
import java.util.stream.Collectors;

import me.limeglass.skungee.bungeecord.handlercontroller.SkungeePlayerHandler;
import me.limeglass.skungee.bungeecord.protocol.ProtocolPlayerManager;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;

public class PlayerYawHandler extends SkungeePlayerHandler {

	public PlayerYawHandler() {
		super(SkungeePacketType.PLAYERYAW);
	}

	@Override
	public Object handlePacket(SkungeePacket packet, InetAddress address) {
		return players.parallelStream()
				.map(player -> ProtocolPlayerManager.getPlayer(player.getUniqueId()))
				.filter(optional -> optional.isPresent())
				.map(optional -> optional.get().getYaw())
				.collect(Collectors.toSet());
	}

}
