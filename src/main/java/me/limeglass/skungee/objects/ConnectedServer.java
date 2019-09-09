package me.limeglass.skungee.objects;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Set;

public class ConnectedServer implements Serializable {

	private static final long serialVersionUID = -4006986305914533724L;
	private final Integer port, maxplayers, heartbeat, recieverPort;
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

	public Boolean hasReciever() {
		return reciever;
	}

	public Integer getRecieverPort() {
		return recieverPort;
	}

	public Integer getPort() {
		return port;
	}

	public InetAddress getAddress() {
		return address;
	}

	public Integer getHeartbeat() {
		return heartbeat;
	}

	public String getName() {
		return name;
	}

	public String getMotd() {
		return motd;
	}

	public Integer getMaxPlayers() {
		return maxplayers;
	}

	public Set<SkungeePlayer> getWhitelistedPlayers() {
		return whitelisted;
	}

}
