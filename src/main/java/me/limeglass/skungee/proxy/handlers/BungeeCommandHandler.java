package me.limeglass.skungee.proxy.handlers;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

import me.limeglass.skungee.Skungee;
import me.limeglass.skungee.common.handlercontroller.SkungeeExecutor;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.common.wrappers.ProxyPlatform;
import me.limeglass.skungee.common.wrappers.SkungeePlatform.Platform;
import me.limeglass.skungee.velocity.SkungeeVelocity;

public class BungeeCommandHandler extends SkungeeExecutor {

	public BungeeCommandHandler() {
		super(Platform.ANY_PROXY, ServerPacketType.SERVERCOMMAND);
	}

	@Override
	public void executePacket(ServerPacket packet, InetAddress address) {
		if (packet.getObject() == null)
			return;
		long delay = (long) packet.getSetObject();
		if (platform.getPlatform() == Platform.BUNGEECORD) {
			net.md_5.bungee.api.ProxyServer server = net.md_5.bungee.api.ProxyServer.getInstance();
			if (delay > 0) {
				int multiplier = 1;
				for (String command : (String[]) packet.getObject()) {
					if (command.startsWith("/"))
						command = command.substring(1);
					String intoRunnable = command;
					((ProxyPlatform)Skungee.getPlatform()).schedule(new Runnable() {
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
		} else {
			SkungeeVelocity velocity = (SkungeeVelocity) platform;
			com.velocitypowered.api.proxy.ProxyServer server = velocity.getProxy();
			if (delay > 0) {
				int multiplier = 1;
				for (String command : (String[]) packet.getObject()) {
					if (command.startsWith("/"))
						command = command.substring(1);
					String intoRunnable = command;
					((ProxyPlatform)Skungee.getPlatform()).schedule(new Runnable() {
						@Override
						public void run() {
							server.getCommandManager().execute(server.getConsoleCommandSource(), intoRunnable);
						}
					}, delay * multiplier, TimeUnit.MILLISECONDS);
					multiplier++;
				}
			} else {
				for (String command : (String[]) packet.getObject()) {
					if (command.startsWith("/"))
						command = command.substring(1);
					server.getCommandManager().execute(server.getConsoleCommandSource(), command);
				}
			}
		}
	}

}
