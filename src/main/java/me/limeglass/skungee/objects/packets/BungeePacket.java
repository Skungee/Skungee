package me.limeglass.skungee.objects.packets;

import java.io.Serializable;

import me.limeglass.skungee.objects.SkungeePlayer;

public class BungeePacket implements Serializable {

	private static final long serialVersionUID = -7377209366283539512L;
	private final boolean returnable;
	private SkungeePlayer[] players;
	private Object settable, object;
	private BungeePacketType type;
	private byte[] password;
	private String name;

	public BungeePacket(boolean returnable, String name) {
		this.returnable = returnable;
		this.name = name;
	}
	
	public BungeePacket(boolean returnable, String name, Object object) {
		this.returnable = returnable;
		this.object = object;
		this.name = name;
	}
	
	public BungeePacket(boolean returnable, BungeePacketType type, Object object) {
		this.returnable = returnable;
		this.object = object;
		this.type = type;
	}
	
	public BungeePacket(boolean returnable, String name, SkungeePlayer... players) {
		this.returnable = returnable;
		this.players = players;
		this.name = name;
	}
	
	public BungeePacket(boolean returnable, BungeePacketType type, SkungeePlayer... players) {
		this.returnable = returnable;
		this.players = players;
		this.type = type;
	}
	
	public BungeePacket(boolean returnable, String name, Object object, SkungeePlayer... players) {
		this.returnable = returnable;
		this.players = players;
		this.object = object;
		this.name = name;
	}
	
	public BungeePacket(boolean returnable, BungeePacketType type, Object object, Object settable) {
		this.returnable = returnable;
		this.settable = settable;
		this.object = object;
		this.type = type;
	}
	
	public BungeePacket(boolean returnable, BungeePacketType type, Object object, SkungeePlayer... players) {
		this.returnable = returnable;
		this.players = players;
		this.object = object;
		this.type = type;
	}
	
	public BungeePacket(boolean returnable, BungeePacketType type, Object object, Object settable, SkungeePlayer... players) {
		this.returnable = returnable;
		this.settable = settable;
		this.players = players;
		this.object = object;
		this.type = type;
	}

	public final boolean isReturnable() {
		return returnable;
	}
	
	public byte[] getPassword() {
		return password;
	}
	
	public void setPassword(byte[] password) {
		this.password = password;
	}
	
	public final Object getObject() {
		return object;
	}
	
	public final String getName() {
		return name;
	}
	
	public final BungeePacketType getType() {
		return type;
	}
	
	public Object getSetObject() {
		return settable;
	}

	public SkungeePlayer[] getPlayers() {
		return players;
	}
	
	public SkungeePlayer getFirstPlayer() {
		for (SkungeePlayer player : players) {
			if (player != null)
				return player;
		}
		return null;
	}

	public void setPlayers(SkungeePlayer[] players) {
		this.players = players;
	}
}
