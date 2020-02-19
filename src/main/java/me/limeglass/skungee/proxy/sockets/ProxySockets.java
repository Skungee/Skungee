package me.limeglass.skungee.proxy.sockets;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import me.limeglass.skungee.EncryptionUtil;
import me.limeglass.skungee.Skungee;
import me.limeglass.skungee.bungeecord.events.BungeeReturnedEvent;
import me.limeglass.skungee.bungeecord.events.BungeeSendingEvent;
import me.limeglass.skungee.common.objects.ProxyPacketResponse;
import me.limeglass.skungee.common.objects.SkungeeServer;
import me.limeglass.skungee.common.packets.ProxyPacket;
import me.limeglass.skungee.common.packets.ProxyPacketType;
import me.limeglass.skungee.common.wrappers.ProxyPlatform;
import me.limeglass.skungee.common.wrappers.SecurityConfiguration;
import me.limeglass.skungee.common.wrappers.SkungeePlatform.Platform;

public class ProxySockets {

	private final ProxyPlatform platform;
	private final ServerTracker tracker;
	private boolean checking;
	private Socket socket;

	public ProxySockets(ProxyPlatform platform) {
		this.tracker = platform.getServerTracker();
		this.platform = platform;
	}

	private Socket getSocketConnection(SkungeeServer server) {
		for (int i = 0; i < platform.getConfiguration().getReceiverTimeout(); i++) {
			try {
				return new Socket(server.getAddress(), server.getRecieverPort());
			} catch (IOException e) {}
		}
		platform.consoleMessage("Could not establish a connection to the reciever on server: " + server.getName());
		return null;
	}

	private ProxyPacketResponse send(SkungeeServer server, ProxyPacket packet) {
		if (server == null) {
			platform.consoleMessage("The server parameter was incorrect or not set while sending bungee " + Skungee.getPacketDebug(packet));
			return null;
		}
		if (tracker.isResponding(server) && server.hasReciever() && !checking) {
			checking = true;
			socket = getSocketConnection(server);
			if (socket == null)
				return null;
			checking = false;
			EncryptionUtil encryption = platform.getEncryptionUtil();
			SecurityConfiguration security = platform.getConfiguration().getSecurityConfiguration();
			String algorithm = security.getCipherAlgorithm();
			String keyString = security.getCipherKey();
			if (platform.getPlatform() == Platform.BUNGEECORD) {
				BungeeSendingEvent event = new BungeeSendingEvent(packet, server);
				net.md_5.bungee.api.ProxyServer.getInstance().getPluginManager().callEvent(event);
				if (event.isCancelled())
					return null;
			}
			boolean debug = true;
			for (String ignore : platform.getConfiguration().getIgnoredDebugPackets()) {
				String name = packet.getName();
				if (name != null && name.equalsIgnoreCase(ignore)) {
					debug = false;
					break;
				}
				try {
					ProxyPacketType type = ProxyPacketType.valueOf(ignore);
					if (type != null && packet.getType() == type) {
						debug = false;
						break;
					}
				} catch (Exception e) {}
			}
			if (debug)
				platform.debugMessage("Sending " + Skungee.getPacketDebug(packet)  + " to server: " + server.getName());
			if (security.isPasswordEnabled()) {
				byte[] password = encryption.serialize(security.getPassword());
				if (security.isPasswordHashed()) {
					if (security.isPasswordFileHashed() && encryption.isFileHashed()) {
						password = encryption.getHashFromFile();
					} else {
						password = encryption.hashPassword();
					}
				}
				if (password != null)
					packet.setPassword(password);
			}
			try {
				socket.setSoTimeout(10000);
				ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
				if (security.hasEncryption()) {
					byte[] serialized = encryption.serialize(packet);
					byte[] encrypted = encryption.encrypt(keyString, algorithm, serialized);
					objectOutputStream.writeObject(encrypted);
				} else {
					objectOutputStream.writeObject(packet);
				}
				ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
				if (packet.isReturnable()) {
					Object value = null;
					if (security.hasEncryption()) {
						value = encryption.decrypt(keyString, algorithm, (byte[]) objectInputStream.readObject());
					} else {
						value = objectInputStream.readObject();
					}
					if (platform.getPlatform() == Platform.BUNGEECORD) {
						BungeeReturnedEvent returning = new BungeeReturnedEvent(packet, value, server);
						net.md_5.bungee.api.ProxyServer.getInstance().getPluginManager().callEvent(returning);
						value = returning.getObject();
					}
					objectOutputStream.close();
					objectInputStream.close();
					socket.close();
					return new ProxyPacketResponse(platform.getPlatform(), server, packet, value);
				}
				objectOutputStream.close();
				objectInputStream.close();
				socket.close();
			} catch (IOException | ClassNotFoundException e) {
				if (security.debug())
					platform.exception(e, "Could not encrypt packet " + Skungee.getPacketDebug(packet));
			}
		} else {
			//TODO wait until it becomes available
		}
		return null;
	}

	public ProxyPacketResponse send(ProxyPacket packet, SkungeeServer server) {
		return sendTo(packet, server).get(0);
	}

	public List<ProxyPacketResponse> sendTo(ProxyPacket packet, SkungeeServer... servers) {
		try {
			return CompletableFuture.supplyAsync(() -> {
				List<ProxyPacketResponse> values = new ArrayList<>();
				for (SkungeeServer server : servers) {
					values.add(send(server, packet));
				}
				return values;
			}).get(5, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<ProxyPacketResponse> sendToAll(ProxyPacket... packets) {
		return tracker.getServers().parallelStream()
				.filter(server -> server.hasReciever())
				.flatMap(server -> Arrays.stream(packets).parallel()
					.flatMap(packet -> sendTo(packet, server).stream()))
				.collect(Collectors.toList());
	}

}
