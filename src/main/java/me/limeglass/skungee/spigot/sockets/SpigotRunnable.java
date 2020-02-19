package me.limeglass.skungee.spigot.sockets;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.eclipse.jdt.annotation.Nullable;

import me.limeglass.skungee.EncryptionUtil;
import me.limeglass.skungee.Skungee;
import me.limeglass.skungee.common.packets.ProxyPacket;
import me.limeglass.skungee.spigot.SkungeeSpigot;
import me.limeglass.skungee.spigot.events.SkungeeReceivedEvent;
import me.limeglass.skungee.spigot.events.SkungeeReturningEvent;

public class SpigotRunnable implements Runnable {

	private final Map<InetAddress, Integer> attempts = new HashMap<>();
	private final Set<InetAddress> blocked = new HashSet<>();
	private final FileConfiguration configuration;
	private final SkungeeSpigot instance;
	private InetAddress address;
	private Socket socket;

	public SpigotRunnable(Socket socket) {
		this.instance = SkungeeSpigot.getInstance();
		this.configuration = instance.getConfig();
		this.address = socket.getInetAddress();
		this.socket = socket;
	}

	@Override
	public void run() {
		if (configuration.getBoolean("security.breaches.enabled", false)) {
			List<String> addresses = configuration.getStringList("security.breaches.blacklisted");
			if (blocked.contains(address) || addresses.contains(address.getHostName()))
				return;
		}
		try {
			String algorithm = configuration.getString("security.encryption.cipherAlgorithm", "AES/CBC/PKCS5Padding");
			String keyString = configuration.getString("security.encryption.cipherKey", "insert 16 length");
			ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
			EncryptionUtil encryption = SkungeeSpigot.getInstance().getEncrypter();
			Object object = objectInputStream.readObject();
			if (object != null) {
				ProxyPacket packet = null;
				try {
					if (configuration.getBoolean("security.encryption.enabled", false)) {
						packet = (ProxyPacket) encryption.decrypt(keyString, algorithm, (byte[]) object);
					} else {
						packet = (ProxyPacket) object;
					}
				} catch (ClassCastException e) {
					instance.consoleMessage("", "Some security settings didn't match for the incoming packet.", "Make sure all your security options on the Spigot servers match the same as in the Bungeecord Skungee configuration.yml", "The packet could not be read, thus being cancelled.");
					if (configuration.getBoolean("security.debug"))
						instance.exception(e, "Could not decrypt packet " + Skungee.getPacketDebug(packet));
					attempt(address, null);
					return;
				}
				SkungeeReceivedEvent event = new SkungeeReceivedEvent(packet);
				Bukkit.getPluginManager().callEvent(event);
				if (event.isCancelled())
					return;
				if (packet.getPassword() != null) {
					if (configuration.getBoolean("security.password.hash", true)) {
						byte[] password = encryption.hashPassword();
						if (configuration.getBoolean("security.password.hashFile", false) && encryption.isFileHashed()) {
							password = encryption.getHashFromFile();
						}
						if (!Arrays.equals(password, packet.getPassword())) {
							incorrectPassword(packet);
							return;
						}
					} else {
						String password = (String) encryption.deserialize(packet.getPassword());
						if (!password.equals(configuration.getString("security.password.password"))){
							incorrectPassword(packet);
							return;
						}
					}
				} else if (configuration.getBoolean("security.password.enabled", false)) {
					incorrectPassword(packet);
					return;
				}
				if (attempts.containsKey(address)) attempts.remove(address);
				Object packetData = SpigotPacketHandler.handlePacket(packet, address);
				if (packetData != null) {
					SkungeeReturningEvent returning = new SkungeeReturningEvent(packet, packetData);
					Bukkit.getPluginManager().callEvent(returning);
					if (returning.isCancelled())
						return;
					packetData = returning.getObject();
					if (configuration.getBoolean("security.encryption.enabled", false)) {
						byte[] serialized = encryption.serialize(packetData);
						byte[] encrypted = encryption.encrypt(keyString, algorithm, serialized);
						objectOutputStream.writeObject(encrypted);
					} else {
						objectOutputStream.writeObject(packetData);
					}
				}
			}
			objectInputStream.close();
			objectOutputStream.close();
		} catch (IOException | ClassNotFoundException e) {
			if (configuration.getBoolean("security.debug"))
				instance.exception(e, "Could not read incoming packet");
		}
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void incorrectPassword(ProxyPacket packet) {
		attempt(address, packet);
		instance.consoleMessage("&cA BungeePacket with an incorrect password has just been recieved and blocked!");
		instance.consoleMessage("&cThe packet came from: " + socket.getInetAddress());
		instance.consoleMessage("&cThe packet type was: " + packet.getType());
		//insert more data maybe
	}

	private void attempt(InetAddress address, @Nullable ProxyPacket packet) {
		if (!configuration.getBoolean("security.breaches.enabled", false))
			return;
		int i = 0;
		if (attempts.containsKey(address))
			i = attempts.get(address);
		i++;
		attempts.put(address, i);
		if (i >= configuration.getInt("security.breaches.attempts", 30)) {
			if (configuration.getBoolean("security.breaches.log", false)) {
				log("", "&cA BungeePacket with an incorrect password has just been recieved and blocked!", "&cThe packet came from: " + socket.getInetAddress());
				if (packet != null) log("&cThe packet type was: " + packet.getType());
			}
			if (configuration.getBoolean("security.breaches.shutdown", false))
				Bukkit.shutdown();
			if (configuration.getBoolean("security.breaches.blockAddress", false)) {
				if (!blocked.contains(address))
					blocked.add(address);
			}
		}
	}

	private void log(String... strings) {
		try {
			Logger logger = Logger.getLogger("log");
			FileHandler handler = new FileHandler(SkungeeSpigot.getInstance().getDataFolder() + File.separator + "breaches.log");
			handler.setFormatter(new SimpleFormatter());
			logger.addHandler(handler);
			for (String string : strings) {
				logger.info(string);
			}
		} catch (SecurityException | IOException e) {
			instance.exception(e, "Error logging a breach.");
		}
	}

}
