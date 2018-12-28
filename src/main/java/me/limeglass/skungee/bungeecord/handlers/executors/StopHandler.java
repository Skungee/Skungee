package me.limeglass.skungee.bungeecord.handlers.executors;

import java.net.InetAddress;
import me.limeglass.skungee.bungeecord.handlercontroller.SkungeeExecutor;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import net.md_5.bungee.api.ProxyServer;

public class StopHandler extends SkungeeExecutor {

	static {
		registerHandler(new StopHandler(), SkungeePacketType.PROXYSTOP);
	}

	@Override
	public void executePacket(SkungeePacket packet, InetAddress address) {
		if (packet.getObject() != null) {
			String message = (String) packet.getObject();
			ProxyServer.getInstance().stop(message);
		} else {
			ProxyServer.getInstance().stop();
		}
	}

}
