package me.limeglass.skungee.bungeecord.handlers;

import java.net.InetAddress;

import me.limeglass.skungee.bungeecord.handlercontroller.SkungeePlayerExecutor;
import me.limeglass.skungee.objects.SkungeeBungeeTitle;
import me.limeglass.skungee.objects.SkungeeTitle;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;

public class TitleHandler extends SkungeePlayerExecutor {

	public TitleHandler() {
		super(SkungeePacketType.TITLE);
	}

	@Override
	public void executePacket(SkungeePacket packet, InetAddress address) {
		if (packet.getObject() == null)
			return;
		SkungeeBungeeTitle title = new SkungeeBungeeTitle((SkungeeTitle)packet.getObject());
		title.send(players);
	}

}
