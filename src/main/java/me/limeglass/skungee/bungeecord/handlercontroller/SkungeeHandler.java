package me.limeglass.skungee.bungeecord.handlercontroller;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Optional;

import java.util.Set;

import me.limeglass.skungee.objects.SkungeePacket;
import me.limeglass.skungee.objects.SkungeePacketType;

public abstract class SkungeeHandler {

	protected static Set<SkungeeHandler> registered = new HashSet<SkungeeHandler>();
	protected SkungeePacketType type = SkungeePacketType.CUSTOM;
	protected SkungeePacket packet;
	protected InetAddress address;
	protected String name;
	
	protected static void registerPacket(SkungeeHandler packet, String name) {
		packet.setName(name);
		if (!registered.contains(packet)) registered.add(packet);
	}
	
	protected static void registerPacket(SkungeeHandler packet, SkungeePacketType type) {
		packet.setType(type);
		registerPacket(packet, type.name());
	}
	
	public static Optional<SkungeeHandler> getHandler(SkungeePacketType type) {
		return registered.parallelStream().filter(packet -> packet.getType() == type).findFirst();
	}
	
	public static Optional<SkungeeHandler> getHandler(String name) {
		return registered.parallelStream().filter(packet -> packet.getName().equals(name)).findFirst();
	}
	
	public SkungeePacketType getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}
	
	public void setType(SkungeePacketType type) {
		this.type = type;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Object callPacket(SkungeePacket packet, InetAddress address) {
		this.packet = packet;
		this.address = address;
		onPacketCall(packet, address);
		return handlePacket(packet, address);
	}
	
	public abstract Object handlePacket(SkungeePacket packet, InetAddress address);
	
	public abstract void onPacketCall(SkungeePacket packet, InetAddress address);
}