package me.limeglass.skungee.bungeecord.handlers;

import java.net.InetAddress;
import me.limeglass.skungee.bungeecord.handlercontroller.SkungeeBungeeHandler;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import net.md_5.bungee.api.ProxyServer;

public class StopHandler extends SkungeeBungeeHandler {

	static {
		registerHandler(new StopHandler(), SkungeePacketType.PROXYSTOP);
	}

	@Override
	public Object handlePacket(SkungeePacket packet, InetAddress address) {
		if (packet.getObject() != null) {
			String message = (String) packet.getObject();
			ProxyServer.getInstance().stop(message);
		} else {
			ProxyServer.getInstance().stop();
		}
		return null;
	}

}
