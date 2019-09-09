package me.limeglass.skungee.bungeecord.handlers.executors;

import java.net.InetAddress;
import me.limeglass.skungee.bungeecord.handlercontroller.SkungeeExecutor;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

public class UnregisterCommandsHandler extends SkungeeExecutor {

	public UnregisterCommandsHandler() {
		super(SkungeePacketType.UNREGISTERCOMMANDS);
	}

	@Override
	public void executePacket(SkungeePacket packet, InetAddress address) {
		if (packet.getObject() == null)
			return;
		PluginManager manager = ProxyServer.getInstance().getPluginManager();
		for (String name : (String[])packet.getObject()) {
			Plugin plugin = manager.getPlugin(name);
			if (plugin != null && !name.equalsIgnoreCase("Skungee"))
				manager.unregisterCommands(plugin);
		}
	}

}
