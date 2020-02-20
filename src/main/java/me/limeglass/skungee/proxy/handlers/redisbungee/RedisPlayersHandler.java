package me.limeglass.skungee.proxy.handlers.redisbungee;

import java.net.InetAddress;
import java.util.Set;
import java.util.stream.Collectors;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;

import me.limeglass.skungee.common.handlercontroller.SkungeeProxyHandler;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.common.player.PacketPlayer;
import me.limeglass.skungee.common.wrappers.SkungeePlatform.Platform;

public class RedisPlayersHandler extends SkungeeProxyHandler<Set<PacketPlayer>> {

	public RedisPlayersHandler() {
		super(Platform.BUNGEECORD, ServerPacketType.REDISPLAYERS);
	}

	@Override
	public Set<PacketPlayer> handlePacket(ServerPacket packet, InetAddress address) {
		return RedisBungee.getApi().getPlayersOnline().stream()
				.map(uuid -> proxy.getPlayer(uuid))
				.map(player -> new PacketPlayer(player.getUUID(), player.getUsername()))
				.collect(Collectors.toSet());
	}

}
