package me.limeglass.skungee.proxy.handlers.executors;

import java.net.InetAddress;

import me.limeglass.skungee.common.handlercontroller.SkungeeExecutor;
import me.limeglass.skungee.common.packets.ProxyPacket;
import me.limeglass.skungee.common.packets.ProxyPacketType;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.proxy.sockets.ProxySockets;

public class SkungeeMessageHandler extends SkungeeExecutor {

	public SkungeeMessageHandler() {
		super(ServerPacketType.SKUNGEEMESSAGES);
	}

	@Override
	public void executePacket(ServerPacket packet, InetAddress address) {
		if (packet.eitherObjectsAreNull())
			return;
		String[] messages = (String[]) packet.getObject();
		String[] channels = (String[]) packet.getSetObject();
		ProxySockets.sendAll(new ProxyPacket(false, ProxyPacketType.SKUNGEEMESSAGES, messages, channels));
	}

}
