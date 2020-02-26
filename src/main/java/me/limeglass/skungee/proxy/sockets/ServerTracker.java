package me.limeglass.skungee.proxy.sockets;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import me.limeglass.skungee.common.objects.SkungeeServer;
import me.limeglass.skungee.common.packets.ProxyPacket;
import me.limeglass.skungee.common.packets.ProxyPacketType;
import me.limeglass.skungee.common.wrappers.ProxyConfiguration;
import me.limeglass.skungee.common.wrappers.ProxyPlatform;

public class ServerTracker {

	private static Set<SkungeeServer> notResponding = new HashSet<>();
	private static Set<SkungeeServer> servers = new HashSet<>();
	private final ProxyPlatform platform;

	public ServerTracker(ProxyPlatform platform) {
		this.platform = platform;
		int timeoutMultiplier = platform.getConfiguration().getTrackerTimeout();
		platform.schedule(() -> {
			if (servers.isEmpty())
				return;
			for (SkungeeServer server : servers) {
				long timeElapsed = System.currentTimeMillis() - server.getLastUpdate();
				if (timeElapsed > timeoutMultiplier * server.getHeartbeat()) {
					notResponding(server);
				}
			}
		}, 1, TimeUnit.SECONDS);
	}

	public void notResponding(SkungeeServer server) {
		if (!notResponding.contains(server)) {
			platform.debugMessage("Server " + server.getName() + " has stopped responding!");
			if (platform.getConfiguration().shouldDisableTracking()) {
				remove(server);
			} else {
				notResponding.add(server);
			}
		}
	}

	/**
	 * Update the server from a Heartbeat packet.
	 * 
	 * @param address The address the heartbeat packet came from.
	 * @return true if the server isn't connected.
	 */
	public boolean update(InetSocketAddress address) {
		Optional<SkungeeServer> connected = servers.stream()
				.filter(server -> server.matches(address))
				.findFirst();
		if (!connected.isPresent())
			return true; //Tells the system this server isn't connected.
		SkungeeServer server = connected.get();
		server.update();
		if (notResponding.contains(server)) {
			notResponding.remove(server);
			platform.debugMessage(server.getName() + " started responding again!");
		}
		globalScripts(server);
		return false;
	}

	private void globalScripts(SkungeeServer server) {
		ProxyConfiguration configuration = platform.getConfiguration();
		if (!configuration.areGlobalScriptsEnabled())
			return;
		File scriptFolder = platform.getScriptsFolder();
		if (scriptFolder == null)
			return;
		if (scriptFolder.listFiles().length <= 0)
			return;
		Map<String, List<String>> data = new HashMap<>();
		String charset = configuration.getGlobalScriptsCharset();
		Charset chars = Charset.defaultCharset();
		if (!charset.equals("default"))
			chars = Charset.forName(charset);
		file : for (File script : scriptFolder.listFiles()) {
			try {
				if (script.isDirectory()) {
					if (script.getName().equalsIgnoreCase(server.getName())) {
						for (File directory : script.listFiles()) {
							data.put(directory.getName(), Files.readAllLines(directory.toPath(), chars));
						}
					}
					continue file;
				}
				data.put(script.getName(), Files.readAllLines(script.toPath(), chars));
			} catch (IOException e) {
				platform.consoleMessage("Charset " + charset + " does not support some symbols in script " + script.getName());
				e.printStackTrace();
			}
		}
		platform.send(new ProxyPacket(false, ProxyPacketType.GLOBALSCRIPTS, data), server);
	}

	/**
	 * Get a registered SkungeeServer by name or using the format ADDRESS:PORT in a string.
	 * 
	 * @param names The name formats of the servers to find.
	 * @return The found servers in an array.
	 */
	public SkungeeServer[] get(String... names) {
		if (servers.isEmpty())
			return null;
		Set<SkungeeServer> servers = new HashSet<>();
		for (String name : names) {
			if (name.contains(":")) {
				String[] addresses = (name.contains(",")) ? name.split(",") : new String[]{name};
				for (String address : addresses) {
					String[] ipPort = address.split(":");
					try {
						InetSocketAddress socketAddress = InetSocketAddress.createUnresolved(ipPort[0], Integer.parseInt(ipPort[1]));
						getByAddress(socketAddress).ifPresent(server -> servers.add(server));
					} catch (IllegalArgumentException e) {
						platform.consoleMessage("The port number was not a valid number or outside of the server's ports: " + Arrays.toString(ipPort));
					}
				}
				continue;
			}
			for (SkungeeServer server : servers) {
				if (server.getName().equalsIgnoreCase(name))
					servers.add(server);
			}
		}
		return servers.toArray(new SkungeeServer[servers.size()]);
	}

	public Optional<SkungeeServer> getByAddress(InetSocketAddress address) {
		return servers.stream()
				.filter(server -> server.matches(address))
				.findFirst();
	}

	public SkungeeServer getLocalByPort(int port) {
		if (servers.isEmpty())
			return null;
		try {
			for (SkungeeServer server : servers) {
				for (Enumeration<NetworkInterface> entry = NetworkInterface.getNetworkInterfaces(); entry.hasMoreElements();) {
					for (Enumeration<InetAddress> addresses = entry.nextElement().getInetAddresses(); addresses.hasMoreElements();) {
						if (addresses.nextElement().getHostAddress().equals(server.getAddress().getHostAddress()) && port == server.getPort()) {
							return server;
						}
					}
				}
			}
		} catch (SocketException exception) {
			platform.exception(exception, "Could not find the system's local host.");
		}
		return null;
	}

	public Set<SkungeeServer> getServers() {
		return servers;
	}

	public boolean isResponding(SkungeeServer server) {
		return !notResponding.contains(server);
	}

	public boolean contains(InetSocketAddress address) {
		return servers.stream().anyMatch(server -> server.matches(address));
	}

	public void add(SkungeeServer server) {
		servers.removeIf(connected -> connected.getAddress().equals(server.getAddress()) && connected.getPort() == server.getPort());
		servers.add(server);
		platform.consoleMessage("Connected to server " + server.getName() + " with port " + server.getPort());
	}

	public void remove(SkungeeServer server) {
		notResponding.remove(server);
		servers.remove(server);
	}

}
