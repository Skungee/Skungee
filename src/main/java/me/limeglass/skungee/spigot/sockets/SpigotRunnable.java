package me.limeglass.skungee.spigot.sockets;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.Base64;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.bukkit.Bukkit;
import org.eclipse.jdt.annotation.Nullable;

import me.limeglass.skungee.spigot.Skungee;
import me.limeglass.skungee.objects.BungeePacket;

public class SpigotRunnable implements Runnable {

	private Socket socket = null;
	private InetAddress address;

	public SpigotRunnable(Socket socket) {
		this.socket = socket;
		this.address = socket.getInetAddress();
	}

	@Override
	public void run() {
		if (Sockets.blocked.contains(address) || Skungee.getInstance().getConfig().getStringList("security.breaches.blacklisted").contains(address.getHostName())) return;
		try {
			ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
			Object object = objectInputStream.readObject();
			if (object != null) {
				BungeePacket packet;
				//TODO Add cipher encryption + change config message.
				try {
					if (Skungee.getInstance().getConfig().getBoolean("security.encryption.enabled", false)) {
						byte[] decoded = Base64.getDecoder().decode((byte[]) object);
						packet = (BungeePacket) Skungee.getEncrypter().deserialize(decoded);
					} else {
						packet = (BungeePacket) object;
					}
				} catch (ClassCastException e) {
					Skungee.consoleMessage("", "Some security settings didn't match for the incoming packet.", "Make sure all your security options on the Spigot servers match the same as in the Bungeecord Skungee config.yml", "The packet could not be read, thus being cancelled.");
					attempt(address, null);
					return;
				}
				if (packet.getPassword() != null) {
					if (Skungee.getInstance().getConfig().getBoolean("security.password.hash", true)) {
						if (Skungee.getInstance().getConfig().getBoolean("security.password.hashFile", false) && Skungee.getEncrypter().isFileHashed()) {
							if (!Arrays.equals(Skungee.getEncrypter().getHashFromFile(), packet.getPassword())) {
								incorrectPassword(packet);
								return;
							}
						} else if (!Arrays.equals(Skungee.getEncrypter().hash(), packet.getPassword())) {
							incorrectPassword(packet);
							return;
						}
					} else {
						String password = (String) Skungee.getEncrypter().deserialize(packet.getPassword());
						if (!password.equals(Skungee.getInstance().getConfig().getString("security.password.password"))){
							incorrectPassword(packet);
							return;
						}
					}
				} else if (Skungee.getInstance().getConfig().getBoolean("security.password.enabled", false)) {
					incorrectPassword(packet);
					return;
				}
				if (Sockets.attempts.containsKey(address)) Sockets.attempts.remove(address);
				Object packetData = SpigotPacketHandler.handlePacket(packet, address);
				if (packetData != null) {
					//TODO Add cipher encryption + change config message.
					if (Skungee.getInstance().getConfig().getBoolean("security.encryption.enabled", false)) {
						byte[] serialized = Skungee.getEncrypter().serialize(packetData);
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
		if (Skungee.getInstance().getConfig().getBoolean("security.breaches.enabled", false)) {
			int attempts = 0;
			if (Sockets.attempts.containsKey(address)) {
				attempts = Sockets.attempts.get(address);
				Sockets.attempts.remove(address, attempts);
			}
			attempts++;
			Sockets.attempts.put(address, attempts);
			if (attempts >= Skungee.getInstance().getConfig().getInt("security.breaches.attempts", 30)) {
				if (Skungee.getInstance().getConfig().getBoolean("security.breaches.log", false)) {
					log("", "&cA BungeePacket with an incorrect password has just been recieved and blocked!", "&cThe packet came from: " + socket.getInetAddress());
					if (packet != null) log("&cThe packet type was: " + packet.getType());
				}
				if (Skungee.getInstance().getConfig().getBoolean("security.breaches.shutdown", false)) {
					Bukkit.shutdown();
				}
				if (Skungee.getInstance().getConfig().getBoolean("security.breaches.blockAddress", false)) {
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