package me.limeglass.skungee.common.packets;

import java.io.Serializable;

import me.limeglass.skungee.common.player.PacketPlayer;

public class ProxyPacket implements Serializable {

	private static final long serialVersionUID = -7377209366283539512L;
	private final boolean returnable;
	private PacketPlayer[] players;
	private Object settable, object;
	private ProxyPacketType type;
	private byte[] password;
	private String name;

	public ProxyPacket(boolean returnable, String name) {
		this.returnable = returnable;
		this.name = name;
	}
	
	public ProxyPacket(boolean returnable, String name, Object object) {
		this.returnable = returnable;
		this.object = object;
		this.name = name;
	}
	
	public ProxyPacket(boolean returnable, ProxyPacketType type, Object object) {
		this.returnable = returnable;
		this.object = object;
		this.type = type;
	}
	
	public ProxyPacket(boolean returnable, String name, PacketPlayer... players) {
		this.returnable = returnable;
		this.players = players;
		this.name = name;
	}
	
	public ProxyPacket(boolean returnable, ProxyPacketType type, PacketPlayer... players) {
		this.returnable = returnable;
		this.players = players;
		this.type = type;
	}
	
	public ProxyPacket(boolean returnable, String name, Object object, PacketPlayer... players) {
		this.returnable = returnable;
		this.players = players;
		this.object = object;
		this.name = name;
	}
	
	public ProxyPacket(boolean returnable, ProxyPacketType type, Object object, Object settable) {
		this.returnable = returnable;
		this.settable = settable;
		this.object = object;
		this.type = type;
	}
	
	public ProxyPacket(boolean returnable, ProxyPacketType type, Object object, PacketPlayer... players) {
		this.returnable = returnable;
		this.players = players;
		this.object = object;
		this.type = type;
	}
	
	public ProxyPacket(boolean returnable, ProxyPacketType type, Object object, Object settable, PacketPlayer... players) {
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
	
	public final ProxyPacketType getType() {
		return type;
	}
	
	public Object getSetObject() {
		return settable;
	}

	public PacketPlayer[] getPlayers() {
		return players;
	}
	
	public PacketPlayer getFirstPlayer() {
		for (PacketPlayer player : players) {
			if (player != null)
				return player;
		}
		return null;
	}

	public void setPlayers(PacketPlayer... players) {
		this.players = players;
	}

}
