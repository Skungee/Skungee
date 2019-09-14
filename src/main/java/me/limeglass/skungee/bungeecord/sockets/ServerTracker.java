package me.limeglass.skungee.bungeecord.sockets;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
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

import me.limeglass.skungee.bungeecord.Skungee;
import me.limeglass.skungee.objects.ConnectedServer;
import me.limeglass.skungee.objects.packets.BungeePacket;
import me.limeglass.skungee.objects.packets.BungeePacketType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.config.Configuration;

public class ServerTracker {

	private static Set<ConnectedServer> notRespondingServers = new HashSet<>();
	private static Map<ConnectedServer, Long> tracker = new HashMap<>();
	private static Set<ConnectedServer> servers = new HashSet<>();

	public static void tracker() {
		ProxyServer.getInstance().getScheduler().schedule(Skungee.getInstance(), new Runnable() {
			@Override
			public void run() {
				if (servers.isEmpty())
					return;
				long trys = Skungee.getConfig().getLong("Tracker.allowedTrys", 4);
				for (ConnectedServer server : servers) {
					if (!tracker.containsKey(server)) {
						tracker.put(server, System.currentTimeMillis() + (5 * server.getHeartbeat()));
						continue;
					}
					long lastupdated = tracker.get(server) + (trys * server.getHeartbeat());
					if (lastupdated < System.currentTimeMillis()) {
						if (!notRespondingServers.contains(server)) {
							notResponding(server);
						}
					}
				}
			}
		}, 0, 1, TimeUnit.SECONDS);
	}

	public static void notResponding(ConnectedServer server) {
		if (server == null)
			return;
		Skungee.debugMessage("Server " + server.getName() + " has stopped responding!");
		if (Skungee.getConfig().getBoolean("Tracker.DisableTracking", false)) {
			remove(server);
		} else {
			notRespondingServers.add(server);
		}
	}

	public static void dump() {
		tracker.clear();
		servers.clear();
		notRespondingServers.clear();
	}

	public static boolean update(InetSocketAddress address) {
		Optional<ConnectedServer> connected = servers.stream()
				.filter(server -> server.getAddress().equals(address.getAddress()))
				.filter(server -> server.getPort() == address.getPort())
				.findFirst();
		if (!connected.isPresent())
			return true;
		ConnectedServer server = connected.get();
		tracker.put(server, System.currentTimeMillis());
		if (notRespondingServers.contains(server)) {
			notRespondingServers.remove(server);
			Skungee.debugMessage(server.getName() + " started responding again!");
		}
		globalScripts(server);
		return false;
	}

	@Deprecated
	public static boolean update(String string) {
		if (get(string) == null)
			return true;
		ConnectedServer server = get(string)[0];
		if (server == null)
			return true; //Tells the system this server isn't connected.
		tracker.put(server, System.currentTimeMillis());
		if (notRespondingServers.contains(server)) {
			notRespondingServers.remove(server);
			Skungee.debugMessage(server.getName() + " started responding again!");
		}
		globalScripts(server);
		return false;
	}

	private static void globalScripts(ConnectedServer server) {
		Configuration configuration = Skungee.getConfig();
		if (!configuration.getBoolean("GlobalScripts.Enabled", true))
			return;
		Skungee instance = Skungee.getInstance();
		if (instance.getScriptsFolder().listFiles().length <= 0)
			return;
		//TODO make this Google's Multiset.
		Map<String, List<String>> data = new HashMap<>();
		String charset = configuration.getString("GlobalScripts.Charset", "default");
		Charset chars = Charset.defaultCharset();
		if (!charset.equals("default"))
			chars = Charset.forName(charset);
		file : for (File script : Skungee.getInstance().getScriptsFolder().listFiles()) {
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
				Skungee.infoMessage("Charset " + charset + " does not support some symbols in script " + script.getName());
				e.printStackTrace();
			}
		}
		BungeeSockets.send(server, new BungeePacket(false, BungeePacketType.GLOBALSCRIPTS, data));
	}

	public static ConnectedServer[] get(String name) {
		if (isEmpty())
			return null;
		for (ConnectedServer server : servers) {
			if (server.getName().equalsIgnoreCase(name))
				return new ConnectedServer[] {server};
		}
		if (name.contains(":")) {
			Set<ConnectedServer> connectedservers = new HashSet<>();
			String[] addresses = (name.contains(",")) ? name.split(",") : new String[]{name};
			address : for (String address : addresses) {
				if (!address.contains(":")) {
					ConnectedServer possiblyNamed = get(address)[0];
					if (possiblyNamed == null)
						connectedservers.add(possiblyNamed);
					continue address;
				}
				String[] ipPort = address.split(":");
				try {
					for (ConnectedServer server : servers) {
						if (server.getAddress().equals(InetAddress.getByName(ipPort[0]))) {
							if (server.getPort() == Integer.parseInt(ipPort[1])) {
								connectedservers.add(server);
							}
						}
					}
				} catch (UnknownHostException e) {
					Skungee.consoleMessage("There was no server found with the address: " + Arrays.toString(ipPort));
				}
			}
			if (connectedservers != null)
				return connectedservers.toArray(new ConnectedServer[connectedservers.size()]);
		}
		return null;
	}

	public static ConnectedServer getByAddress(InetAddress address, int serverPort) {
		if (isEmpty())
			return null;
		for (ConnectedServer server : servers) {
			if (server.getAddress().equals(address) && server.getPort() == serverPort)
				return server;
		}
		return null;
	}

	public static ConnectedServer getLocalByPort(int port) {
		if (isEmpty())
			return null;
		try {
			for (ConnectedServer server : servers) {
				for (Enumeration<NetworkInterface> entry = NetworkInterface.getNetworkInterfaces(); entry.hasMoreElements();) {
					for (Enumeration<InetAddress> addresses = entry.nextElement().getInetAddresses(); addresses.hasMoreElements();) {
						if (addresses.nextElement().getHostAddress().equals(server.getAddress().getHostAddress()) && port == server.getPort()) {
							return server;
						}
					}
				}
			}
		} catch (SocketException exception) {
			Skungee.exception(exception, "Could not find the system's local host.");
		}
		return null;
	}

	public static boolean isEmpty() {
		return servers.isEmpty();
	}

	public static Set<ConnectedServer> getAll() {
		return servers;
	}

	public static boolean isResponding(ConnectedServer server) {
		return !notRespondingServers.contains(server);
	}

	public static boolean contains(InetAddress address, int port) {
		return servers.stream().anyMatch(server -> server.getAddress().equals(address) && server.getPort() == port);
	}

	public static void add(ConnectedServer server) {
		Set<ConnectedServer> toRemove = new HashSet<>();
		for (ConnectedServer connected : servers) {
			if (connected.getAddress().equals(server.getAddress()) && connected.getPort().equals(server.getPort())) {
				toRemove.add(connected);
			}
		}
		for (ConnectedServer connected : toRemove) {
			remove(connected);
		}
		servers.add(server);
		Skungee.consoleMessage("Connected to server " + server.getName() + " with port " + server.getPort());
	}

	public static void remove(ConnectedServer server) {
		tracker.remove(server);
		notRespondingServers.remove(server);
		servers.remove(server);
	}

}
