package me.limeglass.skungee.bungeecord.handlers.executors;

import java.net.InetAddress;
import me.limeglass.skungee.bungeecord.handlercontroller.SkungeeExecutor;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import net.md_5.bungee.api.ProxyServer;

public class EnablePluginsHandler extends SkungeeExecutor {

	static {
		registerHandler(new EnablePluginsHandler(), SkungeePacketType.ENABLEPLUGINS);
	}

	@Override
	public void executePacket(SkungeePacket packet, InetAddress address) {
		ProxyServer.getInstance().getPluginManager().enablePlugins();
	}

}
