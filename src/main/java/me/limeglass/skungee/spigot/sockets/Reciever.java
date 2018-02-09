package me.limeglass.skungee.spigot.sockets;

import java.io.IOException;
import java.net.ServerSocket;

import org.bukkit.Bukkit;

import me.limeglass.skungee.spigot.Skungee;

public class Reciever {
	
	private static ServerSocket reciever;
	
	private static ServerSocket automatic() {
		int port = Skungee.getInstance().getConfig().getInt("Reciever.startingPort", 1000);
		Throwable lastException = null;
		while (port < Skungee.getInstance().getConfig().getInt("Reciever.maxPort", 65534)) {
			try {
				return new ServerSocket(port);
			} catch (IOException e) {
				lastException = e;
			}
			port++;
		}
		if (lastException != null) Skungee.exception(lastException, "Couldn't find a port between " + Skungee.getInstance().getConfig().getInt("Reciever.startingPort", 1000) + " and " + port);
		return null;
	}
	
	public static void setupReciever() {
		Skungee.getInstance().getServer().getScheduler().runTaskAsynchronously(Skungee.getInstance(), new Runnable() {
			@Override
			public void run() {
				try {
					reciever = (Skungee.getInstance().getConfig().getBoolean("Reciever.automatic", true)) ? automatic() : new ServerSocket(Skungee.getInstance().getConfig().getInt("Reciever.port", 1338), 69);
					Skungee.consoleMessage("Reciever established on port " + reciever.getLocalPort());
					Bukkit.getScheduler().runTaskLaterAsynchronously(Skungee.getInstance(), new Runnable() {
						@Override
						public void run() {
							Sockets.connect();
						}
					}, 5);
					while (!reciever.isClosed()) {
						try {
							new Thread(new SpigotRunnable(reciever.accept())).start();
						} catch (IOException e) {
							Skungee.exception(e, "Socket couldn't be accepted.");
						}
					}
				} catch (IOException e) {
					Skungee.exception(e, "ServerSocket couldn't be created on port: " + Skungee.getInstance().getConfig().getInt("Reciever.port", 1337));
				}
			}
		});
	}
	
	public static ServerSocket getReciever() {
		return reciever;
	}
}
