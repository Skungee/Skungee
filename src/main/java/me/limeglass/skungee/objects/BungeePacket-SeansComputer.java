package me.limeglass.skungee.objects;

import java.io.Serializable;

public class BungeePacket implements Serializable {

	private static final long serialVersionUID = -7377209366283539512L;
	private final Boolean returnable;
	private final BungeePacketType type;
	private Object settable = null, object = null;
	private byte[] password = null;
	private SkungeePlayer[] players;

	public BungeePacket(Boolean returnable, BungeePacketType type, Object object) {
		this.returnable = returnable;
		this.object = object;
		this.type = type;
	}
	
	public BungeePacket(Boolean returnable, BungeePacketType type, SkungeePlayer... players) {
		this.returnable = returnable;
		this.players = players;
		this.type = type;
	}
	
	public BungeePacket(Boolean returnable, BungeePacketType type, Object object, Object settable) {
		this.returnable = returnable;
		this.settable = settable;
		this.object = object;
		this.type = type;
	}
	
	public BungeePacket(Boolean returnable, BungeePacketType type, Object object, SkungeePlayer... players) {
		this.returnable = returnable;
		this.players = players;
		this.object = object;
		this.type = type;
	}
	
	public BungeePacket(Boolean returnable, BungeePacketType type, Object object, Object settable, SkungeePlayer... players) {
		this.returnable = returnable;
		this.settable = settable;
		this.players = players;
		this.object = object;
		this.type = type;
	}

	public final Boolean isReturnable() {
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
	
	public final BungeePacketType getType() {
		return type;
	}
	
	public Object getSetObject() {
		return settable;
	}

	public SkungeePlayer[] getPlayers() {
		return players;
	}

	public void setPlayers(SkungeePlayer[] players) {
		this.players = players;
	}
}
