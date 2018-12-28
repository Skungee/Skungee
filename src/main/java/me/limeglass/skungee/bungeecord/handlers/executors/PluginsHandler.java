package me.limeglass.skungee.bungeecord.handlers.executors;

import java.net.InetAddress;
import me.limeglass.skungee.bungeecord.handlercontroller.SkungeeExecutor;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import net.md_5.bungee.api.ProxyServer;

public class PluginsHandler extends SkungeeExecutor {

	static {
		registerHandler(new PluginsHandler(), SkungeePacketType.ENABLEPLUGINS, SkungeePacketType.LOADPLUGINS);
	}

	@Override
	public void executePacket(SkungeePacket packet, InetAddress address) {
		SkungeePacketType type = packet.getType();
		if (type == SkungeePacketType.ENABLEPLUGINS)
			ProxyServer.getInstance().getPluginManager().enablePlugins();
		else if (type == SkungeePacketType.LOADPLUGINS)
			ProxyServer.getInstance().getPluginManager().loadPlugins();
	}

}
