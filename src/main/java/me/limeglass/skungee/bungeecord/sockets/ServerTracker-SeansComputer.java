package me.limeglass.skungee.bungeecord.sockets;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import me.limeglass.skungee.bungeecord.Skungee;
import me.limeglass.skungee.objects.ConnectedServer;
import net.md_5.bungee.api.ProxyServer;

public class ServerTracker {
	
	private static List<ConnectedServer> servers = new ArrayList<ConnectedServer>();
	private static List<ConnectedServer> notRespondingServers = new ArrayList<ConnectedServer>();
	private static HashMap<ConnectedServer, Long> tracker = new HashMap<ConnectedServer, Long>();
	
	public static void tracker() {
		ProxyServer.getInstance().getScheduler().schedule(Skungee.getInstance(), new Runnable() {
			@Override
			public void run() {
				if(!servers.isEmpty()) {
					for(ConnectedServer server : servers) {
						if(tracker.containsKey(server)) {
							long trys = Skungee.getConfig().getLong("Tracker.allowedTrys", 4);
							long lastupdated = tracker.get(server) + (trys * server.getHeartbeat());
							if (lastupdated < System.currentTimeMillis()) {
								if(!notRespondingServers.contains(server)) {
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
		ConnectedServer server = get(string);
		if (server != null) {
			if (tracker.containsKey(server)) {
				tracker.remove(server);
			}
			tracker.put(server, System.currentTimeMillis());
			if (notRespondingServers.contains(server)) {
				notRespondingServers.remove(server);
				Skungee.debugMessage(server.getName() + " started responding again!");
			}
			return false;
		} else {
			return true; //Tells the system this server isn't connected.
		}
		//TODO: else request an update into the tracker.
		//If this is called a server has a heartbeat but Skungee doesn't have the server registered.
	}
	
	public static ConnectedServer get(String server) {
		if(!servers.isEmpty()) {
			for(ConnectedServer s : servers) {
				if (s.getName().equals(server)) {
					return s;
				}
			}
		}
		return null;
	}
	
	public static ConnectedServer getByAddress(InetAddress server) {
		if(!servers.isEmpty()) {
			for(ConnectedServer s : servers) {
				if (s.getAddress() == server) {
					return s;
				}
			}
		}
		return null;
	}
	
	public static Boolean isEmpty() {
		return servers.isEmpty();
	}
	
	public static List<ConnectedServer> getAll() {
		return servers;
	}
	
	public static Boolean contains(ConnectedServer server) {
		if (servers.contains(server)) {
			return true;
		}
		return false;
	}
	
	public static Boolean isResponding(ConnectedServer server) {
		if (notRespondingServers.contains(server)) {
			return false;
		}
		return true;
	}

	public static void add(ConnectedServer server) {
		remove(server);
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