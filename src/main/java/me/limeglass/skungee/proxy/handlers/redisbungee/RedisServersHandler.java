package me.limeglass.skungee.proxy.handlers.redisbungee;

import java.net.InetAddress;
import java.util.List;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;

import me.limeglass.skungee.common.handlercontroller.SkungeeProxyHandler;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.common.wrappers.SkungeePlatform.Platform;

public class RedisServersHandler extends SkungeeProxyHandler<List<String>> {

	public RedisServersHandler() {
		super(Platform.BUNGEECORD, ServerPacketType.REDISSERVERS);
	}

	@Override
	public List<String> handlePacket(ServerPacket packet, InetAddress address) {
		return RedisBungee.getApi().getAllServers();
	}

}
