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

public class RedisServerPlayersHandler extends SkungeeProxyHandler<Set<PacketPlayer>> {

	public RedisServerPlayersHandler() {
		super(Platform.BUNGEECORD, ServerPacketType.REDISSERVERPLAYERS);
	}

	@Override
	public Set<PacketPlayer> handlePacket(ServerPacket packet, InetAddress address) {
		if (packet.getObject() == null)
			return null;
		// The name of the proxy RedisBungee thing.
		return Arrays.stream((String[])packet.getObject()).flatMap(name -> RedisBungee.getApi().getPlayersOnServer(name).stream()
				.map(uuid -> proxy.getPlayer(uuid))
				.map(player -> new PacketPlayer(player.getUUID(), player.getUsername())))
				.collect(Collectors.toSet());
	}

}
