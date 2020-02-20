package me.limeglass.skungee.proxy.handlers.executors;

import java.net.InetAddress;

import me.limeglass.skungee.common.handlercontroller.SkungeeExecutor;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.common.wrappers.ProxyPlatform;
import me.limeglass.skungee.common.wrappers.SkungeePlatform.Platform;

public class ChatHandler extends SkungeeExecutor {

	public ChatHandler() {
		super(Platform.ANY_PROXY, ServerPacketType.PLAYERCHAT);
	}

	@Override
	public void executePacket(ServerPacket packet, InetAddress address) {
		if (packet.getObject() == null)
			return;
		ProxyPlatform proxy = (ProxyPlatform) platform;
		for (String message : (String[]) packet.getObject())
			proxy.getPlayers(packet.getPlayers()).forEach(player -> player.chat(message));
	}

}
