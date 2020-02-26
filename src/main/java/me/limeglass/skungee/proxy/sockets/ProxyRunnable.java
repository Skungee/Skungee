package me.limeglass.skungee.proxy.sockets;

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
import java.util.Optional;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import me.limeglass.skungee.EncryptionUtil;
import me.limeglass.skungee.Skungee;
import me.limeglass.skungee.bungeecord.events.BungeeReceivedEvent;
import me.limeglass.skungee.bungeecord.events.BungeeReturningEvent;
import me.limeglass.skungee.common.handlercontroller.SkungeeHandler;
import me.limeglass.skungee.common.handlercontroller.SkungeeHandlerManager;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.common.wrappers.ProxyPlatform;
import me.limeglass.skungee.common.wrappers.SecurityConfiguration;
import me.limeglass.skungee.common.wrappers.SkungeeConfiguration;
import me.limeglass.skungee.common.wrappers.SkungeePlatform.Platform;
import me.limeglass.skungee.proxy.handlers.SkungeePacketHandler;

public class ProxyRunnable implements Runnable {

	private final Map<InetAddress, Integer> attempts = new HashMap<>();
	private final Set<InetAddress> blocked = new HashSet<>();
	private final ProxyPlatform platform;
	private final InetAddress address;
	private final Socket socket;

	public ProxyRunnable(Socket socket, ProxyPlatform platform) {
		this.address = socket.getInetAddress();
		this.platform = platform;
		this.socket = socket;
	}

	@Override
	public void run() {
		SkungeeConfiguration configuration = platform.getConfiguration();
		SecurityConfiguration security = configuration.getSecurityConfiguration();
		if (security.areBreachesEnabled()) {
			List<String> addresses = security.getBreachAddresses();
			if (!security.breachAddressesAreWhitelist()) {
				if (blocked.contains(address) || addresses.contains(address.getHostName()))
					return;
			} else if (!addresses.contains(address.getHostName()))
				return;
		}
		try {
			String algorithm = security.getCipherAlgorithm();
			String keyString = security.getCipherKey();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
			EncryptionUtil encryption = platform.getEncryptionUtil();
			Object object = objectInputStream.readObject();
			if (object != null) {
				ServerPacket packet = null;
				try {
					if (security.hasEncryption()) {
						packet = (ServerPacket) encryption.decrypt(keyString, algorithm, (byte[]) object);
					} else {
						packet = (ServerPacket) object;
					}
				} catch (ClassCastException e) {
					platform.consoleMessage("", "Some security settings didn't match for the incoming packet.", "Make sure all your security options on the Spigot servers match the same as in the Bungeecord Skungee config.yml", "The packet could not be read, thus being cancelled.");
					attempt(address, null);
					if (security.debug())
						platform.exception(e, "Could not decrypt packet " + packet != null ? Skungee.getPacketDebug(packet) : "");
					return;
				}
				boolean debug = true;
				for (String ignore : configuration.getIgnoredDebugPackets()) {
					String name = packet.getName();
					if (name != null && name.equalsIgnoreCase(ignore)) {
						debug = false;
						break;
					}
					try {
						ServerPacketType type = ServerPacketType.valueOf(ignore);
						if (type != null && packet.getType() == type) {
							debug = false;
							break;
						}
					} catch (Exception e) {}
				}
				if (debug)
					platform.debugMessage("Recieved " + Skungee.getPacketDebug(packet));
				if (platform.getPlatform() == Platform.BUNGEECORD) {
					BungeeReceivedEvent event = new BungeeReceivedEvent(packet, address);
					net.md_5.bungee.api.ProxyServer.getInstance().getPluginManager().callEvent(event);
					if (event.isCancelled())
						return;
				}
				if (packet.getPassword() != null) {
					if (security.isPasswordHashed()) {
						byte[] password = encryption.hashPassword();
						if (security.isPasswordFileHashed() && encryption.isFileHashed()) {
							password = encryption.getHashFromFile();
						}
						if (!Arrays.equals(password, packet.getPassword())) {
							incorrectPassword(packet);
							return;
						}
					} else {
						String password = (String) encryption.deserialize(packet.getPassword());
						if (!password.equals(security.getPassword())) {
							incorrectPassword(packet);
							return;
						}
					}
				} else if (security.isPasswordEnabled()) {
					incorrectPassword(packet);
					return;
				}
				Optional<SkungeeHandler<?>> handler = SkungeeHandlerManager.getHandler(packet);
				Object packetData = SkungeePacketHandler.handlePacket(packet, address);
				if (handler.isPresent() && handler.get().acceptsPlatform(Skungee.getPlatformType()) && handler.get().onPacketCall(packet, packet.getType(), address))
					packetData = handler.get().handlePacket(packet, address);
				if (!handler.get().acceptsPlatform(Skungee.getPlatformType()))
					platform.consoleMessage(Skungee.getPacketDebug(packet) + " is not applicable for this platform " + Skungee.getPlatformType());
				bungee : if (packetData != null && packet.isReturnable()) {
					if (platform.getPlatform() == Platform.BUNGEECORD) {
						BungeeReturningEvent returning = new BungeeReturningEvent(packet, packetData, address);
						net.md_5.bungee.api.ProxyServer.getInstance().getPluginManager().callEvent(returning);
						if (returning.isCancelled())
							break bungee;
						packetData = returning.getObject();
					}
					if (debug)
						platform.debugMessage(Skungee.getPacketDebug(packet) + " is returning data: " + packetData.toString());
					if (security.hasEncryption()) {
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
			if (security.debug())
				platform.exception(e, "Could not read incoming packet");
		}
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void incorrectPassword(ServerPacket packet) {
		attempt(address, packet);
		platform.consoleMessage("&cA SkungeePacket with an incorrect password has just been recieved and blocked!");
		platform.consoleMessage("&cThe packet came from: " + socket.getInetAddress());
		platform.consoleMessage("&cThe packet type was: " + packet.getType());
		//insert more data maybe
	}

	private void attempt(InetAddress address, ServerPacket packet) {
		SecurityConfiguration security = platform.getConfiguration().getSecurityConfiguration();
		if (security.areBreachesEnabled()) {
			int amount = 0;
			if (attempts.containsKey(address)) {
				amount = attempts.get(address);
				attempts.remove(address, amount);
			}
			amount++;
			platform.consoleMessage(amount + "");
			attempts.put(address, amount);
			if (amount >= security.getMaxBreachAttempts()) {
				if (security.shouldLogBreaches()) {
					log("", "&cA BungeePacket with an incorrect password has just been recieved and blocked!", "&cThe packet came from: " + socket.getInetAddress());
					if (packet != null)
						log("&cThe packet type was: " + packet.getType());
				}
				if (security.shouldBreachesShutdown()) {
					platform.shutdown();
				}
				if (security.shouldBreachesBlock()) {
					if (!blocked.contains(address))
						blocked.add(address);
				}
			}
		}
	}

	private void log(String... strings) {
		File breaches = new File(platform.getDataFolder(), "breaches.log");
		try {
			if (!breaches.exists())
				breaches.createNewFile();
			Logger logger = Logger.getLogger("log");
			FileHandler handler = new FileHandler(platform.getDataFolder() + File.separator + "breaches.log");
			handler.setFormatter(new SimpleFormatter());
			logger.addHandler(handler);
			for (String string : strings) {
				logger.info(string);
			}
		} catch (SecurityException | IOException e) {
			platform.exception(e, "Error logging a breach.");
		}
	}

}
