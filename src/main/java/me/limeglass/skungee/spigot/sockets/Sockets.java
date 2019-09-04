package me.limeglass.skungee.spigot.sockets;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;

import me.limeglass.skungee.EncryptionUtil;
import me.limeglass.skungee.UniversalSkungee;
import me.limeglass.skungee.objects.SkungeePlayer;
import me.limeglass.skungee.objects.events.SkungeeReturnedEvent;
import me.limeglass.skungee.objects.events.SkungeeSendingEvent;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import me.limeglass.skungee.spigot.Skungee;

public class Sockets {

	public static Map<InetAddress, Integer> attempts = new HashMap<>();
	public static Set<InetAddress> blocked = new HashSet<>();
	public static Set<SkungeePacket> unsent = new HashSet<>();
	private static boolean restart = true, checking, isConnected;
	public static Long last = System.currentTimeMillis();
	private static int task, heartbeat, keepAlive;
	public static Socket bungeecord;
	
	//TODO create a system to cache failed packets, It already does but it gives up after a few times and lets it go.
	
	public static boolean isConnected() {
		return isConnected;
	}
	
	@SuppressWarnings("deprecation")
	private static void startHeartbeat() {
		task = Bukkit.getScheduler().scheduleAsyncRepeatingTask(Skungee.getInstance(), new Runnable() {
			@Override
			public void run() {
				Boolean answer = (Boolean) send(new SkungeePacket(true, SkungeePacketType.HEARTBEAT, Bukkit.getPort()));
				if (answer != null && answer) {
					stop(true);
				}
			}
		}, 1, Skungee.getInstance().getConfig().getInt("heartbeat", 30));
	}
	
	@SuppressWarnings("deprecation")
	private static void keepAlive() {
		restart = true;
		keepAlive = Bukkit.getScheduler().scheduleAsyncRepeatingTask(Skungee.getInstance(), new Runnable() {
			@SuppressWarnings("resource")
			@Override
			public void run() {
				try {
					new Socket(Skungee.getInstance().getConfig().getString("host", "0.0.0.0"), Skungee.getInstance().getConfig().getInt("port", 1337));
					Bukkit.getScheduler().cancelTask(keepAlive);
					Skungee.consoleMessage("Connection established again!");
					connect();
				} catch (IOException e) {}
			}
		}, 1, Skungee.getInstance().getConfig().getInt("keepAlive", 10) * 20);
	}
	
	public static void connect() {
		Set<SkungeePlayer> whitelisted = new HashSet<SkungeePlayer>();
		for (OfflinePlayer player : Bukkit.getWhitelistedPlayers()) {
			whitelisted.add(new SkungeePlayer(true, player.getUniqueId(), player.getName()));
		}
		FileConfiguration configuration = Skungee.getInstance().getConfig();
		ArrayList<Object> data = new ArrayList<Object>(Arrays.asList(configuration.getBoolean("Reciever.enabled", false), Reciever.getReciever().getLocalPort(), Bukkit.getPort(), whitelisted, configuration.getInt("heartbeat", 30) * 60, Bukkit.getMotd(), Bukkit.getMaxPlayers()));
		Bukkit.getScheduler().runTaskAsynchronously(Skungee.getInstance(), new Runnable() {
			@Override
			public void run() {
				if (getSocketConnection() == null) {
					stop(false);
					restart = true;
				} else {
					for (int i = 0; i < 10; i++) {
						String state = (String) send(new SkungeePacket(true, SkungeePacketType.HANDSHAKE, data));
						if (state != null && (state.equalsIgnoreCase("CONNECTED") || state.equalsIgnoreCase("ALREADY"))) {
							isConnected = true;
							Skungee.consoleMessage("Successfully connected to the Bungeecord Skungee.");
							break;
						}
						Skungee.debugMessage("Ping packet had no response, configurion for the connection to Bungeecord Skungee may not be valid or blocked. Attempting to try again... " + (i + 1) + "/10");
						try {
							Thread.sleep(TimeUnit.SECONDS.toMillis(3));
						} catch (InterruptedException e) {}
					}
					if (isConnected)
						startHeartbeat();
					else
						keepAlive();
				}
			}
		});
	}

	private static Socket getSocketConnection() {
		FileConfiguration configuration = Skungee.getInstance().getConfig();
		for (int i = 0; i < configuration.getInt("maxAttempts", 20); i++) {
			try {
				return new Socket(configuration.getString("host", "0.0.0.0"), configuration.getInt("port", 1337));
			} catch (IOException e) {}
		}
		return null;
	}

	public static Object send(SkungeePacket packet) {
		if (packet.isReturnable())
			return (isConnected) ? send_i(packet) : (packet.getType() == SkungeePacketType.HANDSHAKE) ? send_i(packet) : null;
		if (Skungee.getInstance().getConfig().getBoolean("Queue.enabled", false)) {
			PacketQueue.queue(packet);
		} else {
			Bukkit.getScheduler().runTaskAsynchronously(Skungee.getInstance(), new Runnable() {
				@Override
				public void run() {
					send_i(packet);
				}
			});
		}
		return null;
	}
	
	public static Object send_i(SkungeePacket packet) {
		try {
			if (!checking) {
				checking = true;
				bungeecord = getSocketConnection();
				checking = false;
				FileConfiguration configuration = Skungee.getInstance().getConfig();
				if (bungeecord == null) {
					if (configuration.getBoolean("hault", false)) {
						return send_i(packet);
					} else {
						Skungee.consoleMessage("Could not establish connection to Skungee on the Bungeecord!");
						unsent.add(packet);
						Bukkit.getScheduler().cancelTask(heartbeat);
						stop(restart);
						restart = false;
					}
				} else {
					if (!unsent.isEmpty()) {
						for (SkungeePacket p : unsent) {
							send(p);
							unsent.remove(p);
						}
					}
					EncryptionUtil encryption = new EncryptionUtil(Skungee.getInstance(), true);
					String algorithm = configuration.getString("security.encryption.cipherAlgorithm", "AES/CBC/PKCS5Padding");
					String keyString = configuration.getString("security.encryption.cipherKey", "insert 16 length");
					SkungeeSendingEvent event = new SkungeeSendingEvent(packet);
					Bukkit.getPluginManager().callEvent(event);
					if (event.isCancelled())
						return null;
					if (!configuration.getBoolean("IgnoreSpamPackets", true)) {
						Skungee.debugMessage("Sending " + UniversalSkungee.getPacketDebug(packet));
					} else if (!(packet.getType() == SkungeePacketType.HEARTBEAT)) {
						Skungee.debugMessage("Sending " + UniversalSkungee.getPacketDebug(packet));
					}
					if (configuration.getBoolean("security.password.enabled", false)) {
						byte[] password = encryption.serialize(configuration.getString("security.password.password"));
						if (configuration.getBoolean("security.password.hash", true)) {
							if (configuration.getBoolean("security.password.hashFile", false) && encryption.isFileHashed()) {
								password = encryption.getHashFromFile();
							} else {
								password = encryption.hash();
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
						Bukkit.getPluginManager().callEvent(returned);
						objectOutputStream.close();
						objectInputStream.close();
						bungeecord.close();
						return returned.getObject();
					}
					objectOutputStream.close();
					objectInputStream.close();
					bungeecord.close();
				}
			}
		} catch (ClassNotFoundException | IOException e) {}
		return null;
	}
	
	public static void onPluginDisabling() {
		Bukkit.getScheduler().cancelTask(task);
		Bukkit.getScheduler().cancelTask(heartbeat);
		Bukkit.getScheduler().cancelTask(keepAlive);
		if (bungeecord != null) {
			try {
				bungeecord.close();
			} catch (IOException e) {
				Skungee.exception(e, "&cError closing main socket.");
			}
		}
	}
	
	public static void stop(Boolean reconnect) {
		Bukkit.getScheduler().cancelTask(task);
		Bukkit.getScheduler().cancelTask(heartbeat);
		Bukkit.getScheduler().cancelTask(keepAlive);
		FileConfiguration configuration = Skungee.getInstance().getConfig();
		if (bungeecord != null) {
			try {
				bungeecord.close();
			} catch (IOException e) {
				Skungee.exception(e, "&cError closing main socket.");
			}
		}
		isConnected = false;
		if (reconnect) {
			Skungee.consoleMessage("&6Attempting to reconnect to Skungee...");
			connect();
		} else if (configuration.getBoolean("reconnect", false)) {
			Skungee.consoleMessage("&6Going into keep alive mode...");
			keepAlive();
		} else {
			Skungee.consoleMessage("&cDisconnected from Skungee!");
			Skungee.consoleMessage("Could be incorrect Skungee details, there was no socket found or was denied access. For socket at " + configuration.getString("host", "0.0.0.0") + ":" + configuration.getInt("port", 1337));
		}
	}
}