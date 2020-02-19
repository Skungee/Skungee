package me.limeglass.skungee.spigot.sockets;

import java.io.IOException;
import java.net.ServerSocket;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitScheduler;

import me.limeglass.skungee.spigot.SkungeeSpigot;

public class Reciever {

	private final FileConfiguration configuration;
	private final BukkitScheduler scheduler;
	private final SkungeeSpigot instance;
	private ServerSocket reciever;

	public Reciever(SkungeeSpigot instance) {
		this.instance = instance;
		this.configuration = instance.getConfig();
		this.scheduler = instance.getServer().getScheduler();
		scheduler.runTaskAsynchronously(instance, new Runnable() {
			@Override
			public void run() {
				int port = configuration.getInt("reciever.port", 1338);
				try {
					if (configuration.getBoolean("reciever.automatic", true))
						reciever = find();
					else
						reciever = new ServerSocket(port, 69);
					instance.loadSockets();
					instance.consoleMessage("Reciever established on port " + reciever.getLocalPort());
					while (!reciever.isClosed()) {
						try {
							new Thread(new SpigotRunnable(reciever.accept())).start();
						} catch (IOException e) {
							instance.exception(e, "Socket couldn't be accepted.");
						}
					}
				} catch (IOException e) {
					instance.exception(e, "Reciever couldn't be created on port: " + port);
				}
			}
		});
	}

	public ServerSocket getReciever() {
		return reciever;
	}

	private ServerSocket find() {
		int starting = configuration.getInt("reciever.starting-port", 1000);
		int ending = configuration.getInt("reciever.max-port", 65534);
		Throwable lastException = null;
		while (starting <= configuration.getInt("reciever.max-port", 65534)) {
			try {
				return new ServerSocket(starting);
			} catch (IOException e) {
				lastException = e;
			}
			starting++;
		}
		if (lastException != null)
			instance.exception(lastException, "Couldn't find a port between " + starting + " and " + ending);
		return null;
	}

}
