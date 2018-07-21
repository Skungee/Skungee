package me.limeglass.skungee.bungeecord.handlers;

import java.net.InetAddress;
import me.limeglass.skungee.bungeecord.handlercontroller.SkungeePlayerHandler;
import me.limeglass.skungee.objects.SkungeeBungeeTitle;
import me.limeglass.skungee.objects.SkungeePacket;
import me.limeglass.skungee.objects.SkungeePacketType;
import me.limeglass.skungee.objects.SkungeeTitle;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class TitleHandler extends SkungeePlayerHandler {

	static {
		registerPacket(new TitleHandler(), SkungeePacketType.TITLE);
	}

	@Override
	public Object handlePacket(SkungeePacket packet, InetAddress address) {
		if (packet.getObject() == null) return null;
		SkungeeBungeeTitle title = new SkungeeBungeeTitle((SkungeeTitle)packet.getObject());
		title.send(players.toArray(new ProxiedPlayer[players.size()]));
		return null;
	}
}
