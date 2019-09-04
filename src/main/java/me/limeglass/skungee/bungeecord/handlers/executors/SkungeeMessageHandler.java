package me.limeglass.skungee.bungeecord.handlers.executors;

import java.net.InetAddress;

import me.limeglass.skungee.bungeecord.handlercontroller.SkungeeExecutor;
import me.limeglass.skungee.bungeecord.sockets.BungeeSockets;
import me.limeglass.skungee.objects.packets.BungeePacket;
import me.limeglass.skungee.objects.packets.BungeePacketType;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;

public class SkungeeMessageHandler extends SkungeeExecutor {

	public SkungeeMessageHandler() {
		super(SkungeePacketType.SKUNGEEMESSAGES);
	}

	@Override
	public void executePacket(SkungeePacket packet, InetAddress address) {
		if (packet.eitherObjectsAreNull())
			return;
		String[] messages = (String[]) packet.getObject();
		String[] channels = (String[]) packet.getSetObject();
		BungeeSockets.sendAll(new BungeePacket(false, BungeePacketType.SKUNGEEMESSAGES, messages, channels));
	}

}
