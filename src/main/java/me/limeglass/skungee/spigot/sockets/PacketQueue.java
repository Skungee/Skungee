package me.limeglass.skungee.spigot.sockets;

import java.net.Socket;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitScheduler;

import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.spigot.SkungeeSpigot;

public class PacketQueue {

	private final Set<ServerPacket> queue = new HashSet<>();
	private final BukkitScheduler scheduler;
	private final SkungeeSpigot instance;
	private final Sockets sockets;
	private int task, delay;

	public PacketQueue(FileConfiguration configuration, SkungeeSpigot instance, Sockets sockets) {
		this.delay = configuration.getInt("queue.delay", 100);
		this.scheduler = instance.getServer().getScheduler();
		this.instance = instance;
		this.sockets = sockets;
	}

	public void stop() {
		scheduler.cancelTask(task);
	}

	public Object wait(ServerPacket packet) {
		FutureTask<Object> future = new FutureTask<>(new WaitQueue(packet));
		sockets.getExecutor().execute(future);
		try {
			return future.get();
		} catch (InterruptedException | ExecutionException e) {
			return null;
		}
	}

	@SuppressWarnings("deprecation")
	public void queue(ServerPacket... packets) {
		for (ServerPacket packet : packets)
			queue.add(packet);
		if (scheduler.isCurrentlyRunning(task))
			return;
		task = scheduler.scheduleAsyncRepeatingTask(instance, new Runnable() {
			@Override
			public void run() {
				if (queue.isEmpty()) {
					scheduler.cancelTask(task);
					task = -1;
					return;
				}
				Iterator<ServerPacket> iterator = queue.iterator();
				while (iterator.hasNext()) {
					ServerPacket packet = iterator.next();
					if (System.currentTimeMillis() - sockets.getLastSent() > delay + 1) {
						sockets.send_i(packet);
						iterator.remove();
					}
					try {
						Thread.sleep(delay);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}, 0, 1);
	}

	private class WaitQueue implements Callable<Object> {

		private final ServerPacket packet;
		
		public WaitQueue(ServerPacket packet) {
			this.packet = packet;
		}
		
		@Override
		public Object call() throws Exception {
			while (true) {
				Optional<Socket> optional = sockets.getSocketConnection();
				if (optional.isPresent())
					return sockets.send_i(packet);
				else {
					Thread.sleep(5000);
				}
			}
		}
		
	}

}
