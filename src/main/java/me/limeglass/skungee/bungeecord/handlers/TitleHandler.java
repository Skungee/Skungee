package me.limeglass.skungee.bungeecord.handlers;

import java.net.InetAddress;
import me.limeglass.skungee.bungeecord.handlercontroller.SkungeePlayerHandler;
import me.limeglass.skungee.objects.SkungeeBungeeTitle;
import me.limeglass.skungee.objects.SkungeeTitle;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;

public class TitleHandler extends SkungeePlayerHandler {

	static {
		registerPacket(new TitleHandler(), SkungeePacketType.TITLE);
	}

	@Override
	public Object handlePacket(SkungeePacket packet, InetAddress address) {
		if (packet.getObject() == null) return null;
		SkungeeBungeeTitle title = new SkungeeBungeeTitle((SkungeeTitle)packet.getObject());
		title.send(players);
		return null;
	}
}
