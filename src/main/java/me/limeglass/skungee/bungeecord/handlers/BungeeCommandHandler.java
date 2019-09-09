package me.limeglass.skungee.bungeecord.handlers;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

import me.limeglass.skungee.bungeecord.Skungee;
import me.limeglass.skungee.bungeecord.handlercontroller.SkungeeExecutor;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import net.md_5.bungee.api.ProxyServer;

public class BungeeCommandHandler extends SkungeeExecutor {

	public BungeeCommandHandler() {
		super(SkungeePacketType.BUNGEECOMMAND);
	}

	@Override
	public void executePacket(SkungeePacket packet, InetAddress address) {
		if (packet.getObject() == null)
			return;
		long delay = (long) packet.getSetObject();
		ProxyServer server = ProxyServer.getInstance();
		if (delay > 0) {
			int multiplier = 1;
			for (String command : (String[]) packet.getObject()) {
				if (command.startsWith("/"))
					command = command.substring(1);
				String intoRunnable = command;
				server.getScheduler().schedule(Skungee.getInstance(), new Runnable() {
					@Override
					public void run() {
						server.getPluginManager().dispatchCommand(server.getConsole(), intoRunnable);
					}
				}, delay * multiplier, TimeUnit.MILLISECONDS);
				multiplier++;
			}
		} else {
			for (String command : (String[]) packet.getObject()) {
				if (command.startsWith("/"))
					command = command.substring(1);
				server.getPluginManager().dispatchCommand(server.getConsole(), command);
			}
		}
	}

}
