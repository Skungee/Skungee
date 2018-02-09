package me.limeglass.skungee.spigot.sockets;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;

import me.limeglass.skungee.objects.SkungeePacket;
import me.limeglass.skungee.spigot.Skungee;

public class PacketQueue {
	
	private static Set<SkungeePacket> queue = new HashSet<SkungeePacket>();
	private static Boolean running = true;

	public static void queue(SkungeePacket... packets) {
		for (SkungeePacket packet : packets) queue.add(packet);
	}
	
	public static void start() {
		Bukkit.getScheduler().runTaskAsynchronously(Skungee.getInstance(), new Runnable() {
			@Override
			public void run() {
				while (running) {
					if (queue != null && !queue.isEmpty()) {
						while (queue.iterator().hasNext()) {
							SkungeePacket packet = queue.iterator().next();
							if (Skungee.getInstance().getConfig().getBoolean("Queue.sync")) {
								if (System.currentTimeMillis() - Sockets.lastSent > Skungee.getInstance().getConfig().getInt("Queue.delay", 100)) {
									Sockets.send_i(packet);
									queue.remove(packet);
								}
							} else {
								Sockets.send_i(packet);
								queue.remove(packet);
							}
							try {
								Thread.sleep(Skungee.getInstance().getConfig().getInt("Queue.delay", 100));
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		});
	}
}
