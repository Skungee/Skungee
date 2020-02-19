package me.limeglass.skungee.common.handlercontroller;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;

public class SkungeeHandlerManager {

	private static final Set<SkungeeHandler> registered = new HashSet<>();

	public static void registerHandler(SkungeeHandler handler) {
		registered.add(handler);
	}

	@SuppressWarnings("unchecked")
	public static <T extends SkungeeHandler> Optional<T> getHandler(Class<T> type) {
		return registered.parallelStream()
				.filter(handler -> type.isAssignableFrom(handler.getClass()))
				.map(handler -> (T) handler)
				.findFirst();
	}

	public static Optional<SkungeeHandler> getHandler(ServerPacket packet) {
		Optional<SkungeeHandler> handler = getHandler(packet.getType());
		if (!handler.isPresent() && packet.getName() != null)
			handler = getHandler(packet.getName());
		return handler;
	}

	public static Optional<SkungeeHandler> getHandler(ServerPacketType type) {
		for (SkungeeHandler handler : registered) {
			for (ServerPacketType packetType : handler.getTypes()) {
				if (packetType == type) {
					return Optional.of(handler);
				}
			}
		}
		return Optional.empty();
	}

	public static Optional<SkungeeHandler> getHandler(String name) {
		return registered.parallelStream()
				.filter(handler -> handler.getName().equalsIgnoreCase(name))
				.findFirst();
	}

}
