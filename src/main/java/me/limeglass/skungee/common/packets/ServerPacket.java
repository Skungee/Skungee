package me.limeglass.skungee.common.packets;

import java.io.Serializable;

import me.limeglass.skungee.common.objects.SkungeeEnums.SkriptChangeMode;
import me.limeglass.skungee.common.player.PacketPlayer;

public class ServerPacket implements Serializable {

	private static final long serialVersionUID = -7377209366283539512L;
	private final boolean returnable;
	private SkriptChangeMode changeMode;
	private PacketPlayer[] players;
	private Object settable, object;
	private ServerPacketType type;
	private byte[] password;
	//Used for external packets, extend this SkungeePacket and set the name to then call the name on Bungeecord through handlers.
	//The name or the type must be set or else the packet will become a dummy packet on Bungeecord. (Also needs the handler)
	private String name;
	
	public ServerPacket(boolean returnable) {
		this.returnable = returnable;
	}
	
	public ServerPacket(boolean returnable, String name) {
		this.returnable = returnable;
		this.name = name;
	}
	
	public ServerPacket(boolean returnable, ServerPacketType type) {
		this.returnable = returnable;
		this.type = type;
	}
	
	public ServerPacket(boolean returnable, String name, Object object) {
		this.returnable = returnable;
		this.object = object;
		this.name = name;
	}
	
	public ServerPacket(boolean returnable, String name, PacketPlayer... players) {
		this.returnable = returnable;
		this.players = players;
		this.name = name;
	}
	
	public ServerPacket(boolean returnable, ServerPacketType type, Object object) {
		this.returnable = returnable;
		this.object = object;
		this.type = type;
	}
	
	public ServerPacket(boolean returnable, String name, Object object, Object settable) {
		this.returnable = returnable;
		this.settable = settable;
		this.object = object;
		this.name = name;
	}
	
	public ServerPacket(boolean returnable, ServerPacketType type, PacketPlayer... players) {
		this.returnable = returnable;
		this.players = players;
		this.type = type;
	}
	
	public ServerPacket(boolean returnable, ServerPacketType type, Object object, Object settable) {
		this.returnable = returnable;
		this.settable = settable;
		this.object = object;
		this.type = type;
	}
	
	public ServerPacket(boolean returnable, String name, Object object, PacketPlayer... players) {
		this.returnable = returnable;
		this.players = players;
		this.object = object;
		this.name = name;
	}
	
	public ServerPacket(boolean returnable, ServerPacketType type, Object object, PacketPlayer... players) {
		this.returnable = returnable;
		this.players = players;
		this.object = object;
		this.type = type;
	}
	
	public ServerPacket(boolean returnable, ServerPacketType type, Object object, SkriptChangeMode changeMode) {
		this.returnable = returnable;
		this.changeMode = changeMode;
		this.object = object;
		this.type = type;
	}
	
	public ServerPacket(boolean returnable, ServerPacketType type, Object object, Object settable, PacketPlayer... players) {
		this.returnable = returnable;
		this.settable = settable;
		this.players = players;
		this.object = object;
		this.type = type;
	}
	
	public ServerPacket(boolean returnable, ServerPacketType type, Object object, Object settable, SkriptChangeMode changeMode) {
		this.returnable = returnable;
		this.changeMode = changeMode;
		this.settable = settable;
		this.object = object;
		this.type = type;
	}
	
	public ServerPacket(boolean returnable, ServerPacketType type, Object object, SkriptChangeMode changeMode, PacketPlayer... players) {
		this.returnable = returnable;
		this.changeMode = changeMode;
		this.players = players;
		this.object = object;
		this.type = type;
	}
	
	public ServerPacket(boolean returnable, ServerPacketType type, Object object, Object settable, SkriptChangeMode changeMode, PacketPlayer... players) {
		this.returnable = returnable;
		this.changeMode = changeMode;
		this.settable = settable;
		this.players = players;
		this.object = object;
		this.type = type;
	}

	public boolean isReturnable() {
		return returnable;
	}
	
	public PacketPlayer[] getPlayers() {
		return players;
	}
	
	public void setPlayers(PacketPlayer... players) {
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
	
	public Boolean objectsAreNull() {
		return object == null && settable == null;
	}
	
	public Boolean eitherObjectsAreNull() {
		return object == null || settable == null;
	}
	
	public ServerPacketType getType() {
		return type;
	}

	public String getName() {
		return name;
	}
}
