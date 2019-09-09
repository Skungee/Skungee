package me.limeglass.skungee.bungeecord.handlers.executors;

import java.net.InetAddress;

import me.limeglass.skungee.bungeecord.handlercontroller.SkungeePlayerExecutor;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class ActionbarHandler extends SkungeePlayerExecutor {

	public ActionbarHandler() {
		super(SkungeePacketType.ACTIONBAR);
	}

	@Override
	public void executePacket(SkungeePacket packet, InetAddress address) {
		if (packet.getObject() == null)
			return;
		String message = (String) packet.getObject();
		players.forEach(player -> player.sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message)));
	}

}
