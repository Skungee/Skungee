package me.limeglass.skungee.proxy.handlers.redisbungee;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;

import me.limeglass.skungee.common.handlercontroller.SkungeeProxyHandler;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.common.player.PacketPlayer;
import me.limeglass.skungee.common.wrappers.SkungeePlatform.Platform;

public class RedisProxyPlayersHandler extends SkungeeProxyHandler<Set<PacketPlayer>> {

	public RedisProxyPlayersHandler() {
		super(Platform.BUNGEECORD, ServerPacketType.REDISPROXYPLAYERS);
	}

	@Override
	public Set<PacketPlayer> handlePacket(ServerPacket packet, InetAddress address) {
		if (packet.getObject() == null)
			return null;
		// The name of the proxy RedisBungee thing.
		return Arrays.stream((String[])packet.getObject()).flatMap(name -> RedisBungee.getApi().getPlayersOnProxy(name).stream()
				.map(uuid -> proxy.getPlayer(uuid))
				.map(player -> new PacketPlayer(player.getUUID(), player.getUsername())))
				.collect(Collectors.toSet());
	}

}
