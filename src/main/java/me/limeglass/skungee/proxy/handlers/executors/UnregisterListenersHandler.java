package me.limeglass.skungee.proxy.handlers.executors;

import java.net.InetAddress;

import me.limeglass.skungee.common.handlercontroller.SkungeeExecutor;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

public class UnregisterListenersHandler extends SkungeeExecutor {

	public UnregisterListenersHandler() {
		super(ServerPacketType.UNREGISTERLISTENERS);
	}

	@Override
	public void executePacket(ServerPacket packet, InetAddress address) {
		if (packet.getObject() == null)
			return;
		PluginManager manager = ProxyServer.getInstance().getPluginManager();
		for (String name : (String[])packet.getObject()) {
			Plugin plugin = manager.getPlugin(name);
			if (plugin != null && !name.equalsIgnoreCase("Skungee"))
				manager.unregisterListeners(plugin);
		}
	}

}
