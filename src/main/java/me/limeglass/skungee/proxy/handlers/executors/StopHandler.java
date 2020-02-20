package me.limeglass.skungee.proxy.handlers.executors;

import java.net.InetAddress;

import me.limeglass.skungee.common.handlercontroller.SkungeeExecutor;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.common.wrappers.SkungeePlatform.Platform;
import net.md_5.bungee.api.ProxyServer;

public class StopHandler extends SkungeeExecutor {

	public StopHandler() {
		super(Platform.BUNGEECORD, ServerPacketType.PROXYSTOP);
	}

	@Override
	public void executePacket(ServerPacket packet, InetAddress address) {
		if (packet.getObject() != null) {
			String message = (String) packet.getObject();
			ProxyServer.getInstance().stop(message);
		} else {
			ProxyServer.getInstance().stop();
		}
	}

}
