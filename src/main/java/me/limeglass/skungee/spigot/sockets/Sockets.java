package me.limeglass.skungee.spigot.sockets;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitScheduler;

import me.limeglass.skungee.EncryptionUtil;
import me.limeglass.skungee.Skungee;
import me.limeglass.skungee.common.packets.HandshakePacket;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.common.player.PacketPlayer;
import me.limeglass.skungee.spigot.SkungeeSpigot;
import me.limeglass.skungee.spigot.events.SkungeeReturnedEvent;
import me.limeglass.skungee.spigot.events.SkungeeSendingEvent;

public class Sockets {

	private final int port, attempts, delay, handshake, heartbeat, keepAlive;
	private final Set<ServerPacket> unsent = new HashSet<>(); // Will only ever be effects.
	private long last = System.currentTimeMillis();
	private final FileConfiguration configuration;
	private int heartbeatTask, keepAliveTask;
	private final BukkitScheduler scheduler;
	private final ExecutorService executor;
	private PacketQueue packetQueue;
	private final SkungeeSpigot instance;
	private final Server server;
	private boolean connected;
	private final String host;
	private Socket bungeecord;

	//TODO create a system to cache failed packets, It already does but it gives up after a few times and lets it go.

	public Sockets(SkungeeSpigot instance) {
		this.instance = instance;
		this.server = instance.getServer();
		this.scheduler = server.getScheduler();
		this.configuration = instance.getConfig();
		this.port = configuration.getInt("port", 1337);
		this.executor = Executors.newSingleThreadExecutor();
		this.heartbeat = configuration.getInt("heartbeat", 60);
		this.host = configuration.getString("host", "0.0.0.0");
		this.delay = configuration.getInt("connection.delay", 1000);
		this.attempts = configuration.getInt("connection.attempts", 20);
		this.keepAlive = configuration.getInt("connection.keep-alive", 10) * 20;
		this.handshake = configuration.getInt("connection.handshake-delay", 2000);
		if (configuration.getBoolean("queue.enabled", true))
			this.packetQueue = new PacketQueue(configuration, instance, this);
		connect();
	}

	public long getLastSent() {
		return last;
	}

	public boolean isConnected() {
		return connected;
	}

	public ExecutorService getExecutor() {
		return executor;
	}

	@SuppressWarnings("deprecation")
	public void keepAlive() {
		instance.consoleMessage("&6Going into keep alive mode...");
		keepAliveTask = scheduler.scheduleAsyncRepeatingTask(instance, new Runnable() {
			@SuppressWarnings("resource")
			@Override
			public void run() {
				try {
					new Socket(host, port);
					Bukkit.getScheduler().cancelTask(keepAliveTask);
					instance.consoleMessage("Connection established again!");
					connect();
				} catch (IOException e) {}
			}
		}, 1, keepAlive);
	}

	@SuppressWarnings("deprecation")
	private void connect() {
		Set<PacketPlayer> whitelisted = server.getWhitelistedPlayers().stream()
				.map(player -> new PacketPlayer(player.getUniqueId(), player.getName()))
				.collect(Collectors.toSet());
		Optional<ServerSocket> reciever = instance.getReciever();
		HandshakePacket packet = new HandshakePacket.Builder(whitelisted)
				.withRecieverPort(reciever.isPresent() ? reciever.get().getLocalPort() : -1)
				.hasReciever(configuration.getBoolean("reciever.enabled", false))
				.withMaxPlayers(server.getMaxPlayers())
				.withMotd(server.getMotd())
				.withPort(server.getPort())
				.withHeartbeat(heartbeat)
				.build();
		scheduler.runTaskAsynchronously(instance, () -> {
			Optional<Socket> optional = getSocketConnection();
			if (!optional.isPresent()) {
				instance.consoleMessage("&cThere was no socket found or was denied access at " + host + ":" + port);
				if (configuration.getBoolean("connection.disable", false)) {
					instance.consoleMessage("&cSkungee is disabling...");
					Bukkit.getPluginManager().disablePlugin(instance);
					return;
				}
				keepAlive();
				return;
			}
			bungeecord = optional.get();
			for (int i = 1; i < 6; i++) {
				String state = send(packet, String.class);
				if (state != null && (state.equals("CONNECTED") || state.equals("ALREADY"))) {
					connected = true;
					instance.consoleMessage("Successfully connected to the Skungee on Bungeecord!");
					break;
				}
				instance.consoleMessage("Ping packet had no response, configurion for the connection to Bungeecord Skungee may not be valid or blocked. Attempting to try again... " + i + "/5");
				try {
					Thread.sleep(handshake);
				} catch (InterruptedException e) {}
			}
			if (!connected) {
				Bukkit.getScheduler().cancelTasks(instance);
				keepAlive();
				return;
			}
			heartbeatTask = scheduler.scheduleAsyncRepeatingTask(instance, new Runnable() {
				@Override
				public void run() {
					Object answer = send(new ServerPacket(true, ServerPacketType.HEARTBEAT, server.getPort()));
					if (answer == null || (boolean) answer)
						restart();
				}
			}, 1, heartbeat);
		});
	}

	Optional<Socket> getSocketConnection() {
		if (bungeecord != null && !bungeecord.isClosed())
			return Optional.ofNullable(bungeecord);
		try {
			FutureTask<Optional<Socket>> future = new FutureTask<>(new Callable<Optional<Socket>>() {
				@Override
				public Optional<Socket> call() throws Exception {
					for (int i = 0; i < attempts; i++) {
						try {
							return Optional.of(new Socket(host, port));
						} catch (IOException e) {
							try {
								Thread.sleep(delay);
							} catch (InterruptedException e1) {}
						}
					}
					return Optional.empty();
				}
			});
			executor.execute(future);
			Optional<Socket> optional = future.get();
			if (optional.isPresent())
				bungeecord = optional.get();
			return optional;
		} catch (InterruptedException | ExecutionException e) {
			return Optional.empty();
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T send(ServerPacket packet, Class<T> expected) {
		Object object = send(packet);
		if (object == null)
			return null;
		if (object.getClass().isAssignableFrom(expected))
			return (T) object;
		throw new IllegalArgumentException("The packet return type for " + Skungee.getPacketDebug(packet) + " was not the expected " + expected.getName() +
				", it was " + object.getClass().getName());
	}

	public Object send(ServerPacket packet) {
		new Thread(() -> {
			SkungeeSendingEvent event = new SkungeeSendingEvent(packet);
			Bukkit.getPluginManager().callEvent(event);
		});
		if (packet.isReturnable()) {
			Supplier<Object> supplier = () -> {
				if (connected || packet.getType() == ServerPacketType.HANDSHAKE)
					return send_i(packet);
				return null;
			};
			try {
				return CompletableFuture.supplyAsync(supplier).get(5, TimeUnit.SECONDS);
			} catch (InterruptedException | ExecutionException | TimeoutException e) {
				return supplier.get();
			}
		}
		if (packetQueue != null) { // null if disabled.
			packetQueue.queue(packet);
		} else {
			scheduler.runTaskAsynchronously(instance, new Runnable() {
				@Override
				public void run() {
					send_i(packet);
				}
			});
		}
		return null;
	}

	public Object send_i(ServerPacket packet) {
		Optional<Socket> optional = getSocketConnection();
		if (!optional.isPresent()) {
			if (configuration.getBoolean("hault", false)) {
				return send_i(packet);
			} else {
				if (configuration.getBoolean("queue.infinite-async-queue"))
					return packetQueue.wait(packet);
				instance.consoleMessage("Could not establish connection to Skungee on the Bungeecord!");
				Bukkit.getScheduler().cancelTask(heartbeatTask);
				unsent.add(packet);
				instance.consoleMessage("&6Attempting to reconnect to Skungee...");
				restart();
			}
			return null;
		}
		bungeecord = optional.get();
		try {
			if (!unsent.isEmpty()) {
				Iterator<ServerPacket> iterator = unsent.iterator();
				while (iterator.hasNext()) {
					ServerPacket effect = iterator.next();
					send(effect);
					iterator.remove();
				}
			}
			EncryptionUtil encryption = SkungeeSpigot.getInstance().getEncrypter();
			String algorithm = configuration.getString("security.encryption.cipherAlgorithm", "AES/CBC/PKCS5Padding");
			String keyString = configuration.getString("security.encryption.cipherKey", "insert 16 length");
			if (!configuration.getBoolean("IgnoreSpamPackets", true)) {
				instance.debugMessage("Sending " + Skungee.getPacketDebug(packet));
			} else if (packet.getType() != ServerPacketType.HEARTBEAT) {
				instance.debugMessage("Sending " + Skungee.getPacketDebug(packet));
			}
			if (configuration.getBoolean("security.password.enabled", false)) {
				byte[] password = encryption.serialize(configuration.getString("security.password.password"));
				if (configuration.getBoolean("security.password.hash", true)) {
					if (configuration.getBoolean("security.password.hashFile", false) && encryption.isFileHashed()) {
						password = encryption.getHashFromFile();
					} else {
						password = encryption.hashPassword();
					}
				}
				if (password != null)
					packet.setPassword(password);
			}
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(bungeecord.getOutputStream());
			if (configuration.getBoolean("security.encryption.enabled", false)) {
				byte[] serialized = encryption.serialize(packet);
				byte[] encrypted = encryption.encrypt(keyString, algorithm, serialized);
				objectOutputStream.writeObject(encrypted);
			} else {
				objectOutputStream.writeObject(packet);
			}
			last = System.currentTimeMillis();
			bungeecord.setSoTimeout(10000);
			ObjectInputStream objectInputStream = new ObjectInputStream(bungeecord.getInputStream());
			if (packet.isReturnable()) {
				Object value = null;
				if (configuration.getBoolean("security.encryption.enabled", false)) {
					value = encryption.decrypt(keyString, algorithm, (byte[]) objectInputStream.readObject());
				} else {
					value = objectInputStream.readObject();
				}
				SkungeeReturnedEvent returned = new SkungeeReturnedEvent(packet, value);
				Bukkit.getScheduler().runTaskAsynchronously(SkungeeSpigot.getInstance(), () -> Bukkit.getPluginManager().callEvent(returned));
				objectOutputStream.close();
				objectInputStream.close();
				bungeecord.close();
				return value;
			}
			objectOutputStream.close();
			objectInputStream.close();
			bungeecord.close();
		} catch (ClassNotFoundException | IOException e) {}
		return null;
	}

	public void restart() {
		disconnect();
		connect();
	}

	public void disconnect() {
		Bukkit.getScheduler().cancelTask(heartbeatTask);
		Bukkit.getScheduler().cancelTask(keepAliveTask);
		if (packetQueue != null) {
			packetQueue.stop();
		}
		if (bungeecord != null) {
			try {
				bungeecord.close();
			} catch (IOException e) {
				instance.exception(e, "&cError closing main socket.");
			}
		}
		connected = false;
	}

}
