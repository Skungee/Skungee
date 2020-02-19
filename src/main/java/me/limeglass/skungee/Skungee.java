package me.limeglass.skungee;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import me.limeglass.skungee.common.packets.ProxyPacket;
import me.limeglass.skungee.common.packets.ProxyPacketType;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.player.SkungeePlayer;
import me.limeglass.skungee.common.wrappers.SkungeePlatform;
import me.limeglass.skungee.common.wrappers.SkungeePlatform.Platform;
import me.limeglass.skungee.velocity.SkungeeVelocity;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

public class Skungee {

	private static SkungeePlatform platform;

	public static Platform getPlatformType() {
		return platform.getPlatform();
	}

	public static SkungeePlatform getPlatform() {
		return platform;
	}

	public static void setPlatform(SkungeePlatform platform) throws IllegalAccessException {
		if (Skungee.platform != null)
			throw new IllegalAccessException("The main platform has already been set.");
		Skungee.platform = platform;
	}

	@SuppressWarnings("deprecation")
	public static Map<String, InetSocketAddress> getServers() {
		if (platform.getPlatform() == Platform.BUNGEECORD) {
			return ProxyServer.getInstance().getServers().entrySet().stream()
					.map(entry -> entry.getValue())
					.collect(Collectors.toMap(ServerInfo::getName, ServerInfo::getAddress));
		} else {
			SkungeeVelocity velocity = (SkungeeVelocity) platform;
			return velocity.getProxy().getAllServers().stream()
					.map(server -> server.getServerInfo())
					.collect(Collectors.toMap(com.velocitypowered.api.proxy.server.ServerInfo::getName, com.velocitypowered.api.proxy.server.ServerInfo::getAddress));
		}
	}

	public static String getPacketDebug(ServerPacket packet) {
		String debug = "packet: " + packet.getType();
		if (packet.getObject() != null) {
			if (packet.getObject().getClass().isArray()) {
				debug = debug + " with data: " + Arrays.toString((Object[])packet.getObject());
			} else {
				debug = debug + " with data: " + packet.getObject();
			}
		}
		if (packet.getSetObject() != null) {
			if (packet.getSetObject().getClass().isArray()) {
				debug = debug + " with settable data: " + Arrays.toString((Object[])packet.getSetObject());
			} else {
				debug = debug + " with settable data: " + packet.getSetObject();
			}
		}
		if (packet.getPlayers() != null && packet.getPlayers().length > 0) {
			debug = debug + " for players: " + Arrays.stream(packet.getPlayers()).filter(player -> player != null).map(SkungeePlayer::getName).collect(Collectors.toList());
		}
		if (packet.getChangeMode() != null) {
			debug = debug + " with change mode: " + packet.getChangeMode();
		}
		return debug;
	}

	public static String getPacketDebug(ProxyPacket packet) {
		String debug = "packet: " + packet.getType();
		if (packet.getObject() != null && packet.getType() != ProxyPacketType.GLOBALSCRIPTS) {
			if (packet.getObject().getClass().isArray()) {
				debug = debug + " with data: " + Arrays.toString((Object[])packet.getObject());
			} else {
				debug = debug + " with data: " + packet.getObject();
			}
		}
		if (packet.getSetObject() != null) {
			if (packet.getSetObject().getClass().isArray()) {
				debug = debug + " with settable data: " + Arrays.toString((Object[])packet.getSetObject());
			} else {
				debug = debug + " with settable data: " + packet.getSetObject();
			}
		}
		if (packet.getPlayers() != null && packet.getPlayers().length > 0) {
			debug = debug + " for players: " + Arrays.stream(packet.getPlayers()).filter(player -> player != null).map(SkungeePlayer::getName).collect(Collectors.toList());
		}
		return debug;
	}

}
