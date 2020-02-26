package me.limeglass.skungee.proxy.handlers.redisbungee;

import java.net.InetAddress;
import java.util.Set;
import java.util.stream.Collectors;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;

import me.limeglass.skungee.bungeecord.BungeePlayer;
import me.limeglass.skungee.common.handlercontroller.SkungeeProxyHandler;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.common.wrappers.SkungeePlatform.Platform;

public class RedisPlayerServerHandler extends SkungeeProxyHandler<Set<String>> {

	public RedisPlayerServerHandler() {
		super(Platform.BUNGEECORD, ServerPacketType.REDISPLAYERSERVER);
	}

	@Override
	public Set<String> handlePacket(ServerPacket packet, InetAddress address) {
		return proxy.getPlayers(packet.getPlayers()).parallelStream()
				.map(player -> ((BungeePlayer)player).getPlayer())
				.filter(optional -> optional.isPresent())
				.map(optional -> optional.get())
				.map(player -> RedisBungee.getApi().getServerFor(player.getUniqueId()).getName())
				.collect(Collectors.toSet());
	}

}
