package me.limeglass.skungee.common.objects;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Set;

import me.limeglass.skungee.common.player.PacketPlayer;

public class SkungeeServer implements Serializable {

	private static final long serialVersionUID = -4006986305914533724L;
	private final int port, maxplayers, heartbeat, recieverPort;
	private long update = System.currentTimeMillis();
	private final Set<PacketPlayer> whitelisted;
	private final InetAddress address;
	private final String motd, name;
	private final boolean reciever;

	public SkungeeServer(boolean reciever, int recieverPort, int port, InetAddress address, int heartbeat, String name, String motd, int maxplayers, Set<PacketPlayer> whitelisted) {
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

	public Set<PacketPlayer> getWhitelisted() {
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

	public void update() {
		update = System.currentTimeMillis();
	}

	public long getLastUpdate() {
		return update;
	}

	public boolean matches(InetSocketAddress address) {
		if (!this.address.equals(address.getAddress()))
			return false;
		if (this.port != address.getPort())
			return false;
		return true;
	}

}
