package me.limeglass.skungee.spigot.sockets;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.eclipse.jdt.annotation.Nullable;

import me.limeglass.skungee.EncryptionUtil;
import me.limeglass.skungee.objects.packets.BungeePacket;
import me.limeglass.skungee.spigot.Skungee;

public class SpigotRunnable implements Runnable {

	private InetAddress address;
	private Socket socket;

	public SpigotRunnable(Socket socket) {
		this.socket = socket;
		this.address = socket.getInetAddress();
	}

	@Override
	public void run() {
		FileConfiguration configuration = Skungee.getInstance().getConfig();
		if (configuration.getBoolean("security.breaches.enabled", false)) {
			List<String> addresses = configuration.getStringList("security.breaches.blacklisted");
			if (Sockets.blocked.contains(address) || addresses.contains(address.getHostName())) return;
		}
		try {
			ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
			Object object = objectInputStream.readObject();
			if (object != null) {
				BungeePacket packet;
				try {
					if (configuration.getBoolean("security.encryption.enabled", false)) {
						String keyString = configuration.getString("security.encryption.cipherKey", "insert 16 length");
						String algorithm = configuration.getString("security.encryption.cipherAlgorithm", "AES/CBC/PKCS5Padding");
						EncryptionUtil encyption = Skungee.getInstance().getEncrypter();
						packet = (BungeePacket) encyption.decrypt(keyString, algorithm, (byte[]) object);
					} else {
						packet = (BungeePacket) object;
					}
				} catch (ClassCastException e) {
					Skungee.consoleMessage("", "Some security settings didn't match for the incoming packet.", "Make sure all your security options on the Spigot servers match the same as in the Bungeecord Skungee configuration.yml", "The packet could not be read, thus being cancelled.");
					if (configuration.getBoolean("security.debug"))
						e.printStackTrace();
					attempt(address, null);
					return;
				}
				if (packet.getPassword() != null) {
					if (configuration.getBoolean("security.password.hash", true)) {
						if (configuration.getBoolean("security.password.hashFile", false) && Skungee.getInstance().getEncrypter().isFileHashed()) {
							if (!Arrays.equals(Skungee.getInstance().getEncrypter().getHashFromFile(), packet.getPassword())) {
								incorrectPassword(packet);
								return;
							}
						} else if (!Arrays.equals(Skungee.getInstance().getEncrypter().hash(), packet.getPassword())) {
							incorrectPassword(packet);
							return;
						}
					} else {
						String password = (String) Skungee.getInstance().getEncrypter().deserialize(packet.getPassword());
						if (!password.equals(configuration.getString("security.password.password"))){
							incorrectPassword(packet);
							return;
						}
					}
				} else if (configuration.getBoolean("security.password.enabled", false)) {
					incorrectPassword(packet);
					return;
				}
				if (Sockets.attempts.containsKey(address)) Sockets.attempts.remove(address);
				Object packetData = SpigotPacketHandler.handlePacket(packet, address);
				if (packetData != null) {
					//TODO Add cipher encryption + change configuration message.
					if (configuration.getBoolean("security.encryption.enabled", false)) {
						byte[] serialized = Skungee.getInstance().getEncrypter().serialize(packetData);
						objectOutputStream.writeObject(Base64.getEncoder().encode(serialized));
					} else {
						objectOutputStream.writeObject(packetData);
					}
				}
			}
			objectInputStream.close();
			objectOutputStream.close();
		} catch(IOException | ClassNotFoundException e) {}
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void incorrectPassword(BungeePacket packet) {
		attempt(address, packet);
		Skungee.consoleMessage("&cA BungeePacket with an incorrect password has just been recieved and blocked!");
		Skungee.consoleMessage("&cThe packet came from: " + socket.getInetAddress());
		Skungee.consoleMessage("&cThe packet type was: " + packet.getType());
		//insert more data maybe
	}
	
	private void attempt(InetAddress address, @Nullable BungeePacket packet) {
		FileConfiguration configuration = Skungee.getInstance().getConfig();
		if (configuration.getBoolean("security.breaches.enabled", false)) {
			int attempts = 0;
			if (Sockets.attempts.containsKey(address)) {
				attempts = Sockets.attempts.get(address);
				Sockets.attempts.remove(address, attempts);
			}
			attempts++;
			Sockets.attempts.put(address, attempts);
			if (attempts >= configuration.getInt("security.breaches.attempts", 30)) {
				if (configuration.getBoolean("security.breaches.log", false)) {
					log("", "&cA BungeePacket with an incorrect password has just been recieved and blocked!", "&cThe packet came from: " + socket.getInetAddress());
					if (packet != null) log("&cThe packet type was: " + packet.getType());
				}
				if (configuration.getBoolean("security.breaches.shutdown", false)) {
					Bukkit.shutdown();
				}
				if (configuration.getBoolean("security.breaches.blockAddress", false)) {
					if (!Sockets.blocked.contains(address)) Sockets.blocked.add(address);
				}
			}
		}
	}
	
	private void log(String... strings) {
		try {
			Logger logger = Logger.getLogger("log");
			FileHandler handler = new FileHandler(Skungee.getInstance().getDataFolder() + File.separator + "breaches.log");
			handler.setFormatter(new SimpleFormatter());
			logger.addHandler(handler);
			for (String string : strings) {
				logger.info(string);
			}
		} catch (SecurityException | IOException e) {
			Skungee.exception(e, "Error logging a breach.");
		}
	}
}