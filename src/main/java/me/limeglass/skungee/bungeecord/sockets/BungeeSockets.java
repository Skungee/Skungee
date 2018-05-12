package me.limeglass.skungee.bungeecord.sockets;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import me.limeglass.skungee.objects.BungeePacket;
import me.limeglass.skungee.objects.BungeePacketType;
import me.limeglass.skungee.objects.ConnectedServer;
import net.md_5.bungee.api.ProxyServer;
import me.limeglass.skungee.UniversalSkungee;
import me.limeglass.skungee.bungeecord.Skungee;

public class BungeeSockets {
	
	private static Boolean checking = false;
	public static Socket spigot = null;
	public static Map<InetAddress, Integer> attempts = new HashMap<InetAddress, Integer>();
	public static Set<InetAddress> blocked = new HashSet<InetAddress>();

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
			Skungee.consoleMessage("The server argument was incorrect or not set while sending bungee " + UniversalSkungee.getPacketDebug(packet));
			return null;
		}
		if (ServerTracker.isResponding(server) && server.hasReciever() && !checking) {
			checking = true;
			spigot = getSocketConnection(server);
			if (spigot == null) return null;
			checking = false;
			if (!Skungee.getConfig().getBoolean("IgnoreSpamPackets", true)) {
				Skungee.debugMessage("Sending " + UniversalSkungee.getPacketDebug(packet) + " to server: " + server.getName());
			} else if (!(packet.getType() == BungeePacketType.GLOBALSCRIPTS)) {
				Skungee.debugMessage("Sending " + UniversalSkungee.getPacketDebug(packet) + " to server: " + server.getName());
			}
			if (Skungee.getConfig().getBoolean("security.password.enabled", false)) {
				byte[] password = Skungee.getEncrypter().serialize(Skungee.getConfig().getString("security.password.password"));
				if (Skungee.getConfig().getBoolean("security.password.hash", true)) {
					if (Skungee.getConfig().getBoolean("security.password.hashFile", false) && Skungee.getEncrypter().isFileHashed()) {
						password = Skungee.getEncrypter().getHashFromFile();
					} else {
						password = Skungee.getEncrypter().hash();
					}
				}
				if (password != null) packet.setPassword(password);
			}
			try {
				spigot.setSoTimeout(10000);
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(spigot.getOutputStream());
				//TODO Add cipher encryption + change config message.
				if (Skungee.getConfig().getBoolean("security.encryption.enabled", false)) {
					byte[] serialized = Skungee.getEncrypter().serialize(packet);
					objectOutputStream.writeObject(Base64.getEncoder().encode(serialized));
				} else {
					objectOutputStream.writeObject(packet);
				}
				ObjectInputStream objectInputStream = new ObjectInputStream(spigot.getInputStream());
				if (packet.isReturnable()) {
					//TODO Add cipher encryption + change config message.
					if (Skungee.getConfig().getBoolean("security.encryption.enabled", false)) {
						byte[] decoded = Base64.getDecoder().decode((byte[]) objectInputStream.readObject());
						return Skungee.getEncrypter().deserialize(decoded);
					} else {
						return objectInputStream.readObject();
					}
				}
				objectOutputStream.close();
				objectInputStream.close();
				spigot.close();
			} catch (IOException | ClassNotFoundException e) {}
		} else {
			//TODO wait until it becomes available
		}
		return null;
	}
	
	public static List<Object> send(final BungeePacket packet, final ConnectedServer... servers) {
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
				Boolean found = false;
				for (ConnectedServer server : servers) {
					builder.append(server.getName() + "-" + server.getAddress() + ":" + server.getPort());
					if (server != null && packet != null) {
						found = true;
						send(server, packet);
					}
				}
				if (!found) Skungee.debugMessage("Could not find servers by the names: " + builder.toString());
			}
		});
		return null;
	}
	
	public static List<Object> sendAll(final BungeePacket packet) {
		if (packet.isReturnable()) return ServerTracker.getAll().parallelStream().filter(server -> server.hasReciever()).map(server -> send(server, packet)).collect(Collectors.toList());
		ProxyServer.getInstance().getScheduler().runAsync(Skungee.getInstance(), new Runnable() {
			@Override
			public void run() {
				Iterator<ConnectedServer> iterator = ServerTracker.getAll().iterator();
				while (iterator.hasNext()) {
					ConnectedServer server = iterator.next();
					if (server.hasReciever()) {
						send(server, packet);
					}
				}
			}
		});
		return null;
	}
	
	@SuppressWarnings("null")
	public static Object[] get(BungeePacket packet, ConnectedServer... servers) {
		Object[] returns = null;
		int i = 0;
		for (ConnectedServer server : servers) {
			returns[i] = send(server, packet);
			i++;
		}
		return (returns != null) ? returns : null;
	}
}
