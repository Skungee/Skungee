package me.limeglass.skungee.objects;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Set;

public class ConnectedServer implements Serializable {

	private static final long serialVersionUID = -4006986305914533724L;
	private final int port, maxplayers, heartbeat, recieverPort;
	private final Set<SkungeePlayer> whitelisted;
	private final InetAddress address;
	private final String motd, name;
	private final boolean reciever;

	public ConnectedServer(boolean reciever, int recieverPort, int port, InetAddress address, int heartbeat, String name, String motd, int maxplayers, Set<SkungeePlayer> whitelisted) {
		this.recieverPort = recieverPort;
		this.whitelisted = whitelisted;
		this.maxplayers = maxplayers;
		this.heartbeat = heartbeat;
		this.reciever = reciever;
		this.address = address;
		this.port = port;
		this.name = name;
		this.motd = motd;
	}

	public Set<SkungeePlayer> getWhitelisted() {
		return whitelisted;
	}

	public InetAddress getAddress() {
		return address;
	}

	public boolean hasReciever() {
		return reciever;
	}

	public int getRecieverPort() {
		return recieverPort;
	}

	public int getMaxPlayers() {
		return maxplayers;
	}

	public int getHeartbeat() {
		return heartbeat;
	}

	public String getName() {
		return name;
	}

	public String getMotd() {
		return motd;
	}

	public int getPort() {
		return port;
	}

}
