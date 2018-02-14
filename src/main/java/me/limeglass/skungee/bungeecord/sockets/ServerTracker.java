package me.limeglass.skungee.bungeecord.sockets;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import me.limeglass.skungee.bungeecord.Skungee;
import me.limeglass.skungee.objects.BungeePacket;
import me.limeglass.skungee.objects.BungeePacketType;
import me.limeglass.skungee.objects.ConnectedServer;
import net.md_5.bungee.api.ProxyServer;

public class ServerTracker {
	
	private static Set<ConnectedServer> servers = new HashSet<ConnectedServer>();
	private static Set<ConnectedServer> notRespondingServers = new HashSet<ConnectedServer>();
	private static Map<ConnectedServer, Long> tracker = new HashMap<ConnectedServer, Long>();
	
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
									Skungee.debugMessage("Server " + server.getName() + " has stopped responding!");
									if (Skungee.getConfig().getBoolean("Tracker.DisableTracking", false)) {
										remove(server);
									} else {
										notRespondingServers.add(server);
									}
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
	
	public static void globalScripts(ConnectedServer server) {
		if (Skungee.getConfig().getBoolean("GlobalScripts.Enabled", true) && Skungee.getScriptsFolder().listFiles().length > 0) {
			Map<String, List<String>> data = new HashMap<String, List<String>>();
			for (File script : Skungee.getScriptsFolder().listFiles()) {
				try {
					data.put(script.getName(), Files.readAllLines(script.toPath(), Charset.defaultCharset()));
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
			Set<ConnectedServer> servers = new HashSet<ConnectedServer>();
			String[] addresses = (name.contains(",")) ? name.split(",") : new String[]{name};
			address : for (String address : addresses) {
				if (!address.contains(":")) {
					ConnectedServer possiblyNamed = get(address)[0];
					if (possiblyNamed == null) servers.add(possiblyNamed);
					continue address;
				}
				String[] ipPort = address.split(":");
				try {
					for (ConnectedServer server : servers) {
						if (server.getAddress() == InetAddress.getByName(ipPort[0])) {
							if (server.getPort() == Integer.parseInt(ipPort[1])) {
								servers.add(server);
							}
						}
					}
				} catch (UnknownHostException e) {
					Skungee.consoleMessage("There was no server found with the address: " + Arrays.toString(ipPort));
				}
			}
			if (servers != null) {
				return servers.toArray(new ConnectedServer[servers.size()]);
			}
		}
		return null;
	}
	
	public static ConnectedServer getByAddress(InetAddress address) {
		if (isEmpty()) return null;
		for (ConnectedServer server : servers) {
			if (server.getAddress() == address) {
				return server;
			}
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
		for (ConnectedServer connected : servers) {
			if (connected.getName().equals(server.getName())) {
				if (connected.getPort() == server.getPort()) {
					remove(connected);
				}
			}
		}
		servers.add(server);
		Skungee.consoleMessage("Connected to server " + server.getName() + " with port " + server.getPort());
	}
	
	public static void remove(ConnectedServer server) {
		tracker.remove(server);
		notRespondingServers.remove(server);
		servers.remove(server);
		Skungee.debugMessage("Removed ConnectedServer " + server.getName() + " with port " + server.getPort());
	}
}