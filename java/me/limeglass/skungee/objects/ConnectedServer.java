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
	private final Boolean reciever;

	public ConnectedServer(Boolean reciever, Integer recieverPort, Integer port, InetAddress address, Integer heartbeat, String name, String motd, Integer maxplayers, Set<SkungeePlayer> whitelisted) {
		this.reciever = reciever;
		this.recieverPort = recieverPort;
		this.port = port;
		this.address = address;
		this.heartbeat = heartbeat;
		this.name = name;
		this.motd = motd;
		this.maxplayers = maxplayers;
		this.whitelisted = whitelisted;
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
