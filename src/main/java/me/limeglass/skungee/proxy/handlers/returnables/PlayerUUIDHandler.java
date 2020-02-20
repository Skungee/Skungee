package me.limeglass.skungee.proxy.handlers.returnables;

import java.net.InetAddress;
import java.util.Set;
import java.util.stream.Collectors;

import me.limeglass.skungee.common.handlercontroller.SkungeeProxyHandler;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.common.player.PacketPlayer;
import me.limeglass.skungee.common.wrappers.SkungeePlatform.Platform;

public class PlayerUUIDHandler extends SkungeeProxyHandler<Set<String>> {

	public PlayerUUIDHandler() {
		super(Platform.ANY_PROXY, ServerPacketType.PLAYERUUID);
	}

	@Override
	public Set<String> handlePacket(ServerPacket packet, InetAddress address) {
		// Will attempt to grab the player's UUID just based off their name.
		// This syntax should only be used on offline servers for synchronization.
		return proxy.getPlayers(packet.getPlayers()).stream()
				.map(player -> proxy.getPlayer(new PacketPlayer(null, player.getUsername())))
				.map(player -> player.getUUID() + "")
				.collect(Collectors.toSet());
	}

}
