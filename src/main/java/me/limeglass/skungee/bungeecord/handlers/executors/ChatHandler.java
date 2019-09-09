package me.limeglass.skungee.bungeecord.handlers.executors;

import java.net.InetAddress;

import me.limeglass.skungee.bungeecord.handlercontroller.SkungeePlayerExecutor;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ChatHandler extends SkungeePlayerExecutor {

	public ChatHandler() {
		super(SkungeePacketType.PLAYERCHAT);
	}

	@Override
	public void executePacket(SkungeePacket packet, InetAddress address) {
		if (packet.getObject() == null)
			return;
		for (ProxiedPlayer player : players) {
			for (String msg : (String[]) packet.getObject()) {
				player.chat(ChatColor.stripColor(msg));
			}
		}
	}

}
