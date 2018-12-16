package me.limeglass.skungee.bungeecord.sockets;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
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
import java.util.Set;
import java.util.concurrent.TimeUnit;

import me.limeglass.skungee.bungeecord.Skungee;
import me.limeglass.skungee.objects.ConnectedServer;
import me.limeglass.skungee.objects.packets.BungeePacket;
import me.limeglass.skungee.objects.packets.BungeePacketType;
import net.md_5.bungee.api.ProxyServer;

public class ServerTracker {
	
	private static Set<ConnectedServer> notRespondingServers = new HashSet<ConnectedServer>();
	private static Map<ConnectedServer, Long> tracker = new HashMap<ConnectedServer, Long>();
	private static Set<ConnectedServer> servers = new HashSet<ConnectedServer>();
	
	public static void tracker() {
		ProxyServer.getInstance().getScheduler().schedule(Skungee.getInstance(), new Runnable() {
			@Override
			public void run() {
				if (!servers.isEmpty()) {
					for (ConnectedServer server : servers) {
						if (tracker.containsKey(server)) {
							long trys = Skungee.getConfig().getLong("Tracker.allowedTrys", 4);
							long lastupdated = tracker.get(server) + (trys * server.getHeartbeat());
							if (lastupdated < System.currentTimeMillis()) {
								if (!notRespondingServers.contains(server)) {
									notResponding(server);
								}
							}
						} else {
							tracker.put(server, System.currentTimeMillis() + (5 * server.getHeartbeat()));
						}
					}
				}
			}
		}, 1, 1, TimeUnit.SECONDS);
	}
	
	public static void notResponding(ConnectedServer server) {
		if (server == null) return;
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
	
	public static Boolean update(String string) {
		if (get(string) == null) return true;
		ConnectedServer server = get(string)[0];
		if (server != null) {
			tracker.put(server, System.currentTimeMillis());
			if (notRespondingServers.contains(server)) {
				notRespondingServers.remove(server);
				Skungee.debugMessage(server.getName() + " started responding again!");
			}
			globalScripts(server);
			return false;
		} else {
			return true; //Tells the system this server isn't connected.
		}
	}
	
	private static void globalScripts(ConnectedServer server) {
		if (Skungee.getConfig().getBoolean("GlobalScripts.Enabled", true) && Skungee.getInstance().getScriptsFolder().listFiles().length > 0) {
			Map<String, List<String>> data = new HashMap<String, List<String>>();
			String charset = Skungee.getConfig().getString("GlobalScripts.Charset", "default");
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
					e.printStackTrace();
				}
			}
			BungeeSockets.send(server, new BungeePacket(false, BungeePacketType.GLOBALSCRIPTS, data));
		}
	}
	
	public static ConnectedServer[] get(String name) {
		if (isEmpty()) return null;
		for (ConnectedServer server : servers) {
			if (server.getName().equalsIgnoreCase(name)) {
				return new ConnectedServer[] {server};
			}
		}
		if (name.contains(":")) {
			Set<ConnectedServer> connectedservers = new HashSet<ConnectedServer>();
			String[] addresses = (name.contains(",")) ? name.split(",") : new String[]{name};
			address : for (String address : addresses) {
				if (!address.contains(":")) {
					ConnectedServer possiblyNamed = get(address)[0];
					if (possiblyNamed == null) connectedservers.add(possiblyNamed);
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
			if (connectedservers != null) {
				return connectedservers.toArray(new ConnectedServer[connectedservers.size()]);
			}
		}
		return null;
	}
	
	public static ConnectedServer getByAddress(InetAddress address, int serverPort) {
		if (isEmpty()) return null;
		for (ConnectedServer server : servers) {
			if (server.getAddress().equals(address) && server.getPort() == serverPort) {
				return server;
			}
		}
		return null;
	}
	
	public static ConnectedServer getLocalByPort(int port) {
		if (isEmpty()) return null;
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
	
	public static Boolean isEmpty() {
		return servers.isEmpty();
	}
	
	public static Set<ConnectedServer> getAll() {
		return servers;
	}
	
	public static Boolean contains(ConnectedServer server) {
		return servers.contains(server);
	}
	
	public static Boolean isResponding(ConnectedServer server) {
		return !notRespondingServers.contains(server);
	}

	public static void add(ConnectedServer server) {
		Set<ConnectedServer> toRemove = new HashSet<ConnectedServer>();
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