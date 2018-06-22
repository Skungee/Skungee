package me.limeglass.skungee.bungeecord.handler;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Optional;

import java.util.Set;
import com.google.common.reflect.Reflection;

import me.limeglass.skungee.objects.SkungeePacket;
import me.limeglass.skungee.objects.SkungeePacketType;
import me.limeglass.skungee.bungeecord.Skungee;
import me.limeglass.skungee.spigot.utils.ReflectionUtil;

public abstract class SkungeeHandler {
	
	protected static Set<SkungeeHandler> registered = new HashSet<SkungeeHandler>();
	protected SkungeePacketType type = SkungeePacketType.CUSTOM;
	protected String name;
	
	protected static void registerPacket(SkungeeHandler packet, String name) {
		packet.setName(name);
		if (!registered.contains(packet)) registered.add(packet);
	}
	
	protected static void registerPacket(SkungeeHandler packet, SkungeePacketType type) {
		packet.setType(type);
		registerPacket(packet, type.name());
	}
	
	public static void load() {
		Set<Class<? extends SkungeeHandler>> classes = ReflectionUtil.getSubTypesOf(Skungee.getInstance(), SkungeeHandler.class, "me.limeglass.skungee.bungeecord.handler");
		Reflection.initialize(classes.toArray(new Class[classes.size()]));
	}
	
	public static Optional<SkungeeHandler> getPacket(SkungeePacketType type) {
		return registered.parallelStream().filter(packet -> packet.getType() == type).findFirst();
	}
	
	public static Optional<SkungeeHandler> getPacket(String name) {
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
	
	public abstract Object handlePacket(SkungeePacket packet, InetAddress address);
}