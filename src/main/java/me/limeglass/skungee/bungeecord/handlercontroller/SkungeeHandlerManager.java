package me.limeglass.skungee.bungeecord.handlercontroller;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;

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

	public static Optional<SkungeeHandler> getHandler(SkungeePacket packet) {
		Optional<SkungeeHandler> handler = getHandler(packet.getType());
		if (!handler.isPresent() && packet.getName() != null)
			handler = getHandler(packet.getName());
		return handler;
	}

	public static Optional<SkungeeHandler> getHandler(SkungeePacketType type) {
		for (SkungeeHandler handler : registered) {
			for (SkungeePacketType packetType : handler.getTypes()) {
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
