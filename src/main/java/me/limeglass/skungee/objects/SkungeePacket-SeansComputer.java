package me.limeglass.skungee.objects;

import java.io.Serializable;

public class SkungeePacket implements Serializable {

	private static final long serialVersionUID = -7377209366283539512L;
	private final SkungeePacketType type;
	private final Boolean returnable;
	private SkriptChangeMode changeMode;
	private SkungeePlayer[] players;
	private Object settable = null, object = null;
	private byte[] password = null;

	public SkungeePacket(Boolean returnable, SkungeePacketType type) {
		this.returnable = returnable;
		this.type = type;
	}
	
	public SkungeePacket(Boolean returnable, SkungeePacketType type, Object object) {
		this.returnable = returnable;
		this.object = object;
		this.type = type;
	}
	
	public SkungeePacket(Boolean returnable, SkungeePacketType type, SkungeePlayer... players) {
		this.returnable = returnable;
		this.players = players;
		this.type = type;
	}
	
	public SkungeePacket(Boolean returnable, SkungeePacketType type, Object object, Object settable) {
		this.returnable = returnable;
		this.settable = settable;
		this.object = object;
		this.type = type;
	}
	
	public SkungeePacket(Boolean returnable, SkungeePacketType type, Object object, SkungeePlayer... players) {
		this.returnable = returnable;
		this.players = players;
		this.object = object;
		this.type = type;
	}
	
	public SkungeePacket(Boolean returnable, SkungeePacketType type, Object object, SkriptChangeMode changeMode) {
		this.returnable = returnable;
		this.changeMode = changeMode;
		this.object = object;
		this.type = type;
	}
	
	public SkungeePacket(Boolean returnable, SkungeePacketType type, Object object, Object settable, SkungeePlayer... players) {
		this.returnable = returnable;
		this.settable = settable;
		this.players = players;
		this.object = object;
		this.type = type;
	}
	
	public SkungeePacket(Boolean returnable, SkungeePacketType type, Object object, Object settable, SkriptChangeMode changeMode) {
		this.returnable = returnable;
		this.changeMode = changeMode;
		this.settable = settable;
		this.object = object;
		this.type = type;
	}
	
	public SkungeePacket(Boolean returnable, SkungeePacketType type, Object object, SkriptChangeMode changeMode, SkungeePlayer... players) {
		this.returnable = returnable;
		this.changeMode = changeMode;
		this.players = players;
		this.object = object;
		this.type = type;
	}
	
	public SkungeePacket(Boolean returnable, SkungeePacketType type, Object object, Object settable, SkriptChangeMode changeMode, SkungeePlayer... players) {
		this.returnable = returnable;
		this.changeMode = changeMode;
		this.settable = settable;
		this.players = players;
		this.object = object;
		this.type = type;
	}

	public Boolean isReturnable() {
		return returnable;
	}
	
	public SkungeePlayer[] getPlayers() {
		return players;
	}
	
	public void setPlayers(SkungeePlayer... players) {
		this.players = players;
	}
	
	public SkriptChangeMode getChangeMode() {
		return changeMode;
	}
	
	public void setChangeMode(SkriptChangeMode changeMode) {
		this.changeMode = changeMode;
	}
	
	public byte[] getPassword() {
		return password;
	}
	
	public void setPassword(byte[] password) {
		this.password = password;
	}
	
	public Object getObject() {
		return object;
	}
	
	public void setObject(Object object) {
		this.object = object;
	}
	
	public Object getSetObject() {
		return settable;
	}
	
	public void setSettableObject(Object settable) {
		this.settable = settable;
	}
	
	public SkungeePacketType getType() {
		return type;
	}
}
