package me.limeglass.skungee.proxy.handlers.redisbungee;

import java.net.InetAddress;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;

import me.limeglass.skungee.common.handlercontroller.SkungeeProxyHandler;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.common.wrappers.SkungeePlatform.Platform;

public class RedisServerIDHandler extends SkungeeProxyHandler<String> {

	public RedisServerIDHandler() {
		super(Platform.BUNGEECORD, ServerPacketType.REDISSERVERID);
	}

	@Override
	public String handlePacket(ServerPacket packet, InetAddress address) {
		return RedisBungee.getApi().getServerId();
	}

}
