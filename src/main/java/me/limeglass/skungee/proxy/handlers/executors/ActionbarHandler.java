package me.limeglass.skungee.proxy.handlers.executors;

import java.net.InetAddress;

import me.limeglass.skungee.common.handlercontroller.SkungeeExecutor;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.common.wrappers.ProxyPlatform;
import me.limeglass.skungee.common.wrappers.SkungeePlatform.Platform;

public class ActionbarHandler extends SkungeeExecutor {

	public ActionbarHandler() {
		super(Platform.ANY_PROXY, ServerPacketType.ACTIONBAR);
	}

	@Override
	public void executePacket(ServerPacket packet, InetAddress address) {
		if (packet.getObject() == null)
			return;
		String message = (String) packet.getObject();
		((ProxyPlatform)platform).getPlayers(packet.getPlayers()).forEach(player -> player.sendActionbar(message));
	}

}
