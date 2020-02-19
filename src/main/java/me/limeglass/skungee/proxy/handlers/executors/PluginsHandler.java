package me.limeglass.skungee.proxy.handlers.executors;

import java.net.InetAddress;

import me.limeglass.skungee.common.handlercontroller.SkungeeExecutor;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import net.md_5.bungee.api.ProxyServer;

public class PluginsHandler extends SkungeeExecutor {

	public PluginsHandler() {
		super(ServerPacketType.ENABLEPLUGINS, ServerPacketType.LOADPLUGINS);
	}

	@Override
	public void executePacket(ServerPacket packet, InetAddress address) {
		ServerPacketType type = packet.getType();
		if (type == ServerPacketType.ENABLEPLUGINS)
			ProxyServer.getInstance().getPluginManager().enablePlugins();
		else if (type == ServerPacketType.LOADPLUGINS)
			ProxyServer.getInstance().getPluginManager().loadPlugins();
	}

}
