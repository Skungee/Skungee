package me.limeglass.skungee.bungeecord.sockets;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import me.limeglass.skungee.EncryptionUtil;
import me.limeglass.skungee.UniversalSkungee;
import me.limeglass.skungee.bungeecord.Skungee;
import me.limeglass.skungee.objects.ConnectedServer;
import me.limeglass.skungee.objects.events.BungeeReturnedEvent;
import me.limeglass.skungee.objects.events.BungeeSendingEvent;
import me.limeglass.skungee.objects.packets.BungeePacket;
import me.limeglass.skungee.objects.packets.BungeePacketType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.config.Configuration;

public class BungeeSockets {

	public static Map<InetAddress, Integer> attempts = new HashMap<>();
	public static Set<InetAddress> blocked = new HashSet<>();
	private static boolean checking;
	public static Socket spigot;

	private static Socket getSocketConnection(ConnectedServer server) {
		for (int i = 0; i < Skungee.getConfig().getInt("Recievers.allowedTrys", 5); i++) {
			try {
				return new Socket(server.getAddress(), server.getRecieverPort());
			} catch (IOException e) {}
		}
		Skungee.consoleMessage("Could not establish a connection to the reciever on server: " + server.getName());
		return null;
	}

	public static Object send(ConnectedServer server, BungeePacket packet) {
		if (server == null) {
			Skungee.consoleMessage("The server parameter was incorrect or not set while sending bungee " + UniversalSkungee.getPacketDebug(packet));
			return null;
		}
		if (ServerTracker.isResponding(server) && server.hasReciever() && !checking) {
			checking = true;
			spigot = getSocketConnection(server);
			if (spigot == null)
				return null;
			checking = false;
			Configuration configuration = Skungee.getConfig();
			EncryptionUtil encryption = Skungee.getEncrypter();
			String algorithm = configuration.getString("security.encryption.cipherAlgorithm", "AES/CBC/PKCS5Padding");
			String keyString = configuration.getString("security.encryption.cipherKey", "insert 16 length");
			BungeeSendingEvent event = new BungeeSendingEvent(packet, server);
			ProxyServer.getInstance().getPluginManager().callEvent(event);
			if (event.isCancelled())
				return null;
			if (!configuration.getBoolean("IgnoreSpamPackets", true)) {
				Skungee.debugMessage("Sending " + UniversalSkungee.getPacketDebug(packet) + " to server: " + server.getName());
			} else if (packet.getType() != BungeePacketType.GLOBALSCRIPTS) {
				Skungee.debugMessage("Sending " + UniversalSkungee.getPacketDebug(packet) + " to server: " + server.getName());
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
			try {
				spigot.setSoTimeout(10000);
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(spigot.getOutputStream());
				if (configuration.getBoolean("security.encryption.enabled", false)) {
					byte[] serialized = encryption.serialize(packet);
					byte[] encrypted = encryption.encrypt(keyString, algorithm, serialized);
					objectOutputStream.writeObject(encrypted);
				} else {
					objectOutputStream.writeObject(packet);
				}
				ObjectInputStream objectInputStream = new ObjectInputStream(spigot.getInputStream());
				if (packet.isReturnable()) {
					Object value = null;
					if (configuration.getBoolean("security.encryption.enabled", false)) {
						value = encryption.decrypt(keyString, algorithm, (byte[]) objectInputStream.readObject());
					} else {
						value = objectInputStream.readObject();
					}
					BungeeReturnedEvent returning = new BungeeReturnedEvent(packet, value, server);
					ProxyServer.getInstance().getPluginManager().callEvent(returning);
					objectOutputStream.close();
					objectInputStream.close();
					spigot.close();
					return returning.getObject();
				}
				objectOutputStream.close();
				objectInputStream.close();
				spigot.close();
			} catch (IOException | ClassNotFoundException e) {
				if (configuration.getBoolean("security.debug"))
					Skungee.exception(e, "Could not encrypt packet " + UniversalSkungee.getPacketDebug(packet));
			}
		} else {
			//TODO wait until it becomes available
		}
		return null;
	}

	public static List<Object> send(BungeePacket packet, ConnectedServer... servers) {
		if (packet.isReturnable()) {
			List<Object> values = new ArrayList<Object>();
			for (ConnectedServer server : servers) {
				values.add(send(server, packet));
			}
			return values;
		}
		ProxyServer.getInstance().getScheduler().runAsync(Skungee.getInstance(), new Runnable() {
			@Override
			public void run() {
				StringBuilder builder = new StringBuilder();
				boolean found = false;
				for (ConnectedServer server : servers) {
					if (server == null)
						continue;
					builder.append(server.getName() + "-" + server.getAddress() + ":" + server.getPort());
					if (packet != null) {
						found = true;
						send(server, packet);
					}
				}
				if (!found) Skungee.debugMessage("Could not find servers by the names: " + builder.toString());
			}
		});
		return null;
	}

	public static List<Object> sendAll(BungeePacket packet) {
		return ServerTracker.getAll().parallelStream()
				.filter(server -> server.hasReciever())
				.map(server -> send(server, packet))
				.collect(Collectors.toList());
	}

	public static Object[] get(BungeePacket packet, ConnectedServer... servers) {
		Object[] returns = new Object[servers.length];
		for (int i = 0; i < servers.length; i++) {
			returns[i] = send(servers[i], packet);
		}
		return returns;
	}

}
