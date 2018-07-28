package me.limeglass.skungee.bungeecord.handlercontroller;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Optional;

import java.util.Set;

import me.limeglass.skungee.bungeecord.Skungee;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;

public abstract class SkungeeHandler {

	protected static Set<SkungeeHandler> registered = new HashSet<SkungeeHandler>();
	protected SkungeePacketType type = SkungeePacketType.CUSTOM;
	protected SkungeePacket packet;
	protected InetAddress address;
	protected String name;
	
	protected static void registerHandler(SkungeeHandler handler, String name) {
		handler.setName(name);
		if (!registered.contains(handler)) registered.add(handler);
	}
	
	protected static void registerHandler(SkungeeHandler handler, SkungeePacketType type) {
		handler.setType(type);
		registerHandler(handler, type.name());
	}
	
	public static Optional<SkungeeHandler> getHandler(SkungeePacketType type) {
		return registered.parallelStream().filter(handler -> handler.getType() == type).findFirst();
	}
	
	public static Optional<SkungeeHandler> getHandler(String name) {
		return registered.parallelStream().filter(handler -> handler.getName().equals(name)).findFirst();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Optional<T> getHandler(Class<? extends SkungeeHandler> type) {
		return (Optional<T>) registered.parallelStream().filter(handler -> type.isAssignableFrom(handler.getClass())).findFirst();
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
		String string = toString(packet);
		if (string != null) Skungee.debugMessage("Recieved " + string);
		if (!onPacketCall(packet, address)) return null;
		return handlePacket(packet, address);
	}
	
	public abstract String toString(SkungeePacket packet);
	
	public abstract Object handlePacket(SkungeePacket packet, InetAddress address);
	
	public abstract Boolean onPacketCall(SkungeePacket packet, InetAddress address);
}