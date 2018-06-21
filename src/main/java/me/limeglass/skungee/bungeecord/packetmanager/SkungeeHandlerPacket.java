package me.limeglass.skungee.bungeecord.packetmanager;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Optional;

import org.reflections.Reflections;

import java.util.Set;
import com.google.common.reflect.Reflection;

import me.limeglass.skungee.objects.SkungeePacket;
import me.limeglass.skungee.objects.SkungeePacketType;
import me.limeglass.skungee.spigot.Skungee;

public abstract class SkungeeHandlerPacket {

	protected static Set<SkungeeHandlerPacket> registered = new HashSet<SkungeeHandlerPacket>();
	protected SkungeePacketType type = SkungeePacketType.CUSTOM;
	protected String name;
	
	protected static void registerPacket(SkungeeHandlerPacket packet, String name) {
		packet.setName(name);
		if (!registered.contains(packet)) registered.add(packet);
	}
	
	protected static void registerPacket(SkungeeHandlerPacket packet, SkungeePacketType type) {
		packet.setType(type);
		registerPacket(packet, type.name());
	}
	
	public static void load() {
		Reflections reflections = new Reflections(Skungee.getInstance().getPackageName());
		Set<Class<? extends SkungeeHandlerPacket>> classes = reflections.getSubTypesOf(SkungeeHandlerPacket.class);
		Reflection.initialize(classes.toArray(new Class[classes.size()]));
	}
	
	public static Optional<SkungeeHandlerPacket> getPacket(SkungeePacketType type) {
		return registered.parallelStream().filter(packet -> packet.getType() == type).findFirst();
	}
	
	public static Optional<SkungeeHandlerPacket> getPacket(String name) {
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