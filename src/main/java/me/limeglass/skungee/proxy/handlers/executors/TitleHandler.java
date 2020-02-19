package me.limeglass.skungee.proxy.handlers.executors;

import java.net.InetAddress;

import me.limeglass.skungee.common.handlercontroller.SkungeeBungeePlayerExecutor;
import me.limeglass.skungee.common.objects.SkungeeBungeeTitle;
import me.limeglass.skungee.common.objects.SkungeeTitle;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;

public class TitleHandler extends SkungeeBungeePlayerExecutor {

	public TitleHandler() {
		super(ServerPacketType.TITLE);
	}

	@Override
	public void executePacket(ServerPacket packet, InetAddress address) {
		if (packet.getObject() == null)
			return;
		SkungeeBungeeTitle title = new SkungeeBungeeTitle((SkungeeTitle)packet.getObject());
		title.send(players);
	}

}
