package me.limeglass.skungee.proxy.handlers.redisbungee;

import java.net.InetAddress;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;

import me.limeglass.skungee.common.handlercontroller.SkungeeExecutor;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.common.wrappers.SkungeePlatform.Platform;

public class RedisProxyCommandHandler extends SkungeeExecutor {

	public RedisProxyCommandHandler() {
		super(Platform.BUNGEECORD, ServerPacketType.REDISPROXYCOMMAND);
	}

	@Override
	public void executePacket(ServerPacket packet, InetAddress address) {
		if (packet.getObject() == null)
			return;
		for (String command : (String[]) packet.getObject()) {
			if (packet.getSetObject() != null) {
				for (String server : (String[]) packet.getSetObject()) {
					RedisBungee.getApi().sendProxyCommand(server, command);
				}
			} else {
				RedisBungee.getApi().sendProxyCommand(command);
			}
		}
	}

}
