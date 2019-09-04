package me.limeglass.skungee.bungeecord.handlers;

import java.net.InetAddress;

import me.limeglass.skungee.bungeecord.handlercontroller.SkungeePlayerExecutor;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PlayerMessageHandler extends SkungeePlayerExecutor {

	public PlayerMessageHandler() {
		super(SkungeePacketType.MESSAGEPLAYERS);
	}

	@Override
	public void executePacket(SkungeePacket packet, InetAddress address) {
		if (packet.getObject() == null)
			return;
		for (ProxiedPlayer player : players) {
			for (String msg : (String[]) packet.getObject()) {
				player.sendMessage(TextComponent.fromLegacyText(msg));
			}
		}
	}

}
