package me.limeglass.skungee.proxy.handlers.redisbungee;

import java.net.InetAddress;
import java.util.Set;
import java.util.stream.Collectors;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;

import me.limeglass.skungee.bungeecord.BungeePlayer;
import me.limeglass.skungee.common.handlercontroller.SkungeeProxyHandler;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.common.player.PacketPlayer;
import me.limeglass.skungee.common.wrappers.SkungeePlatform.Platform;

public class RedisPlayerNameHandler extends SkungeeProxyHandler<Set<PacketPlayer>> {

	public RedisPlayerNameHandler() {
		super(Platform.BUNGEECORD, ServerPacketType.REDISPLAYERNAME);
	}

	@Override
	public Set<PacketPlayer> handlePacket(ServerPacket packet, InetAddress address) {
		return proxy.getPlayers(packet.getPlayers()).parallelStream()
				.map(player -> ((BungeePlayer)player).getPlayer())
				.filter(optional -> optional.isPresent())
				.map(optional -> optional.get())
				.map(player -> new PacketPlayer(player.getUniqueId(), RedisBungee.getApi().getNameFromUuid(player.getUniqueId(), true)))
				.collect(Collectors.toSet());
	}

}
