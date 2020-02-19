package me.limeglass.skungee.proxy.handlers.returnables;

import java.net.InetAddress;

import me.limeglass.skungee.common.handlercontroller.SkungeeExecutor;
import me.limeglass.skungee.common.packets.ProxyPacket;
import me.limeglass.skungee.common.packets.ProxyPacketType;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.common.wrappers.SkungeePlatform.Platform;
import me.limeglass.skungee.proxy.sockets.ProxySockets;
import me.limeglass.skungee.proxy.sockets.ServerTracker;

public class EvaluateHandler extends SkungeeExecutor {

	public EvaluateHandler() {
		super(Platform.ANY_PROXY, ServerPacketType.EVALUATE);
	}

	@Override
	public void executePacket(ServerPacket packet, InetAddress address) {
		if (packet.getObject() == null)
			return;
		if (packet.getSetObject() == null)
			return;
		String[] evaluations = (String[]) packet.getObject();
		String[] evalServers = (String[]) packet.getSetObject();
		ProxyPacket evalPacket = new ProxyPacket(false, ProxyPacketType.EVALUATE, evaluations);
		for (String server : evalServers) {
			ProxySockets.send(evalPacket, ServerTracker.get(server));
		}
	}

}
