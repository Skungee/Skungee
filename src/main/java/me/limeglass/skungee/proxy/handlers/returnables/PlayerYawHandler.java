package me.limeglass.skungee.proxy.handlers.returnables;

import java.net.InetAddress;
import java.util.Set;
import java.util.stream.Collectors;

import me.limeglass.skungee.bungeecord.BungeePlayer;
import me.limeglass.skungee.common.handlercontroller.SkungeeProxyHandler;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.common.wrappers.SkungeePlatform.Platform;
import me.limeglass.skungee.proxy.protocol.ProtocolPlayerManager;

public class PlayerYawHandler extends SkungeeProxyHandler<Set<Number>> {

	public PlayerYawHandler() {
		super(Platform.ANY_PROXY, ServerPacketType.PLAYERYAW);
	}

	@Override
	public Set<Number> handlePacket(ServerPacket packet, InetAddress address) {
		return proxy.getPlayers(packet.getPlayers()).stream()
				.map(player -> ((BungeePlayer)player).getPlayer())
				.filter(optional -> optional.isPresent())
				.map(optional -> optional.get())
				.map(player -> ProtocolPlayerManager.getPlayer(player.getUniqueId()))
				.filter(optional -> optional.isPresent())
				.map(optional -> optional.get().getYaw())
				.collect(Collectors.toSet());
	}

}
