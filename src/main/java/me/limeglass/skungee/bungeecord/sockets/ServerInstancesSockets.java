package me.limeglass.skungee.bungeecord.sockets;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.Base64;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.config.Configuration;
import me.limeglass.skungee.bungeecord.Skungee;
import me.limeglass.skungee.objects.packets.ServerInstancesPacket;

public class ServerInstancesSockets {

	private static boolean checking;
	public static Socket bootstrap;
	private static int port;

	public static void setInstancesPort(int port) {
		ServerInstancesSockets.port = port;
	}

	public static void shutdown() {
		if (bootstrap == null)
			return;
		try {
			bootstrap.close();
		} catch (IOException e) {}
	}

	private static Socket getBootstap() {
		for (int i = 0; i < Skungee.getConfig().getInt("Recievers.allowedTrys", 5); i++) {
			try {
				return new Socket(InetAddress.getLocalHost(), port);
			} catch (IOException e) {}
		}
		Skungee.consoleMessage("Could not establish a connection to the ServerInstances Bootstap.");
		return null;
	}

	public static Object sendPacket(ServerInstancesPacket packet) {
		if (port <= 0)
			Skungee.consoleMessage("The recieving system for ServerInstances has not been setup yet, ", "make sure that you have ServerInstances installed or configured properly.");
		try {
			if (bootstrap == null || !bootstrap.isConnected() || bootstrap.isClosed()) {
				if (checking) {
					if (!packet.isReturnable()) {
						//Wait until the bootstrap finder has finished.
						//This should already be on an async thread if it made it here.
						while (checking) {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {}
						}
						sendPacket(packet);
					}
					return null;
				}
				//Initialize a new bootstrap instance.
				checking = true;
				bootstrap = getBootstap();
				if (bootstrap == null)
					return null;
				bootstrap.setSoTimeout(10000);
				checking = false;
			}
			Skungee.debugMessage("Sending " + getPacketDebug(packet) + " to the ServerInstances Bootstrap.");
			Configuration configuration = Skungee.getConfig();
			//Security setup
			if (configuration.getBoolean("security.password.enabled", false)) {
				byte[] password = Skungee.getEncrypter().serialize(configuration.getString("security.password.password"));
				if (configuration.getBoolean("security.password.hash", true)) {
					if (configuration.getBoolean("security.password.hashFile", false) && Skungee.getEncrypter().isFileHashed()) {
						password = Skungee.getEncrypter().getHashFromFile();
					} else {
						password = Skungee.getEncrypter().hash();
					}
				}
				if (password != null)
					packet.setPassword(password);
			}
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(bootstrap.getOutputStream());
			if (configuration.getBoolean("security.encryption.enabled", false)) {
				byte[] serialized = Skungee.getEncrypter().serialize(packet);
				objectOutputStream.writeObject(Base64.getEncoder().encode(serialized));
			} else {
				objectOutputStream.writeObject(packet);
			}
			//NOTE: There is a bug where if a ServerInstances server stays in the config.yml of Bungeecord this can error, strange stuff.
			ObjectInputStream objectInputStream = new ObjectInputStream(bootstrap.getInputStream());
			if (packet.isReturnable()) {
				if (configuration.getBoolean("security.encryption.enabled", false)) {
					byte[] decoded = Base64.getDecoder().decode((byte[]) objectInputStream.readObject());
					return Skungee.getEncrypter().deserialize(decoded);
				} else {
					return objectInputStream.readObject();
				}
			}
			objectOutputStream.close();
			objectInputStream.close();
		} catch (IOException | ClassNotFoundException e) {
			Skungee.exception(e, "Could not send packet " + packet.getType());
		}
		return null;
	}

	private static String getPacketDebug(ServerInstancesPacket packet) {
		String debug = "Packet: " + packet.getType();
		if (packet.getObject() != null) {
			if (packet.getObject().getClass().isArray()) {
				debug = debug + " with data: " + Arrays.toString((Object[])packet.getObject());
			} else {
				debug = debug + " with data: " + packet.getObject();
			}
		}
		if (packet.getSetObject() != null) {
			if (packet.getSetObject().getClass().isArray()) {
				debug = debug + " with settable data: " + Arrays.toString((Object[])packet.getSetObject());
			} else {
				debug = debug + " with settable data: " + packet.getSetObject();
			}
		}
		return debug;
	}

	public static Object send(ServerInstancesPacket packet) {
		if (packet == null)
			return null;
		if (packet.isReturnable()) {
			return sendPacket(packet);
		}
		ProxyServer.getInstance().getScheduler().runAsync(Skungee.getInstance(), new Runnable() {
			@Override
			public void run() {
				sendPacket(packet);
			}
		});
		return null;
	}

}
