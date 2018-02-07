package me.limeglass.skungee;

import java.util.Arrays;
import java.util.stream.Collectors;

import me.limeglass.skungee.objects.BungeePacket;
import me.limeglass.skungee.objects.BungeePacketType;
import me.limeglass.skungee.objects.SkungeePacket;
import me.limeglass.skungee.objects.SkungeePlayer;

public class UniversalSkungee {

	private static Boolean isBungeecord = false;
	
	public static Boolean isBungeecord() {
		return isBungeecord;
	}
	
	public static void setBungeecord(Boolean bungeecord) {
		isBungeecord = bungeecord;
	}
	
	public static String getPacketDebug(SkungeePacket packet) {
		String debug = "Recieved packet: " + packet.getType();
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
		if (packet.getPlayers() != null) {
			debug = debug + " for players: " + Arrays.stream(packet.getPlayers()).map(SkungeePlayer::getName).collect(Collectors.toList());
		}
		return debug;
	}
	
	public static String getPacketDebug(BungeePacket packet) {
		String debug = "packet: " + packet.getType();
		if (packet.getObject() != null && packet.getType() != BungeePacketType.GLOBALSCRIPTS) {
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
		return debug;
	}
}