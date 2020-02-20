package me.limeglass.skungee.proxy.handlers.executors;

import java.net.InetAddress;

import me.limeglass.skungee.common.handlercontroller.SkungeeExecutor;
import me.limeglass.skungee.common.packets.ProxyPacket;
import me.limeglass.skungee.common.packets.ProxyPacketType;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.common.wrappers.ProxyPlatform;
import me.limeglass.skungee.common.wrappers.SkungeePlatform.Platform;

public class SkungeeMessageHandler extends SkungeeExecutor {

	public SkungeeMessageHandler() {
		super(Platform.ANY_PROXY, ServerPacketType.SKUNGEEMESSAGES);
	}

	@Override
	public void executePacket(ServerPacket packet, InetAddress address) {
		if (packet.eitherObjectsAreNull())
			return;
		String[] messages = (String[]) packet.getObject();
		String[] channels = (String[]) packet.getSetObject();
		((ProxyPlatform)platform).sendToAll(new ProxyPacket(false, ProxyPacketType.SKUNGEEMESSAGES, messages, channels));
	}

}
