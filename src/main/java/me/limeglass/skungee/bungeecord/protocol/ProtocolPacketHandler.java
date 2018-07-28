package me.limeglass.skungee.bungeecord.protocol;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import me.limeglass.skungee.bungeecord.Skungee;
import me.limeglass.skungee.objects.packets.ProtocolPacket;

public abstract class ProtocolPacketHandler {

	protected static List<ProtocolPacketHandler> registered = new ArrayList<ProtocolPacketHandler>();
	@SuppressWarnings("deprecation")
	protected static int protocol = Skungee.getInstance().getProxy().getProtocolVersion();
	protected int packetId, maxProtocol, minProtocol;
	protected String name;
	
	protected static void registerProtocolHandler(ProtocolPacketHandler handler, String name, int packetId, int minProtocol, int maxProtocol) {
		handler.setName(name);
		handler.setPacketId(packetId);
		handler.setMinProtocol(minProtocol);
		handler.setMaxProtocol(maxProtocol);
		if (!registered.contains(handler)) registered.add(handler);
	}
	
	public static List<ProtocolPacketHandler> getHandlers(int packetId) {
		return registered.parallelStream().filter(handler -> handler.getPacketId() == packetId).collect(Collectors.toList());
	}
	
	public int getPacketId() {
		return packetId;
	}
	
	public void setPacketId(int packetId) {
		this.packetId = packetId;
	}
	
	public int getMaxProtocol() {
		return maxProtocol;
	}
	
	public void setMaxProtocol(int maxProtocol) {
		this.maxProtocol = maxProtocol;
	}
	
	public int getMinProtocol() {
		return minProtocol;
	}
	
	public void setMinProtocol(int minProtocol) {
		this.minProtocol = minProtocol;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	// Return true if the packet should be cancelled.
	public abstract boolean handlePacket(ProtocolPacket packet);
	
}