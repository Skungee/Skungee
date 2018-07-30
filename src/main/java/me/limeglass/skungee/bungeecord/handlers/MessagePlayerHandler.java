package me.limeglass.skungee.bungeecord.handlers;

import java.net.InetAddress;
import me.limeglass.skungee.bungeecord.handlercontroller.SkungeePlayerHandler;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class MessagePlayerHandler extends SkungeePlayerHandler {

	static {
		registerHandler(new MessagePlayerHandler(), SkungeePacketType.MESSAGEPLAYERS);
	}

	@Override
	public Object handlePacket(SkungeePacket packet, InetAddress address) {
		if (packet.getObject() == null) return null;
		for (ProxiedPlayer player : players) {
			for (String msg : (String[]) packet.getObject()) {
				player.sendMessage(new TextComponent(msg));
			}
		}
		return null;
	}
	
}