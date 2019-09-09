package me.limeglass.skungee.bungeecord.handlers.returnables;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;
import me.limeglass.skungee.bungeecord.handlercontroller.SkungeeBungeeHandler;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

public class ServersMOTDHandler extends SkungeeBungeeHandler {

	public ServersMOTDHandler() {
		super(SkungeePacketType.SERVERMOTD);
	}

	@Override
	public Object handlePacket(SkungeePacket packet, InetAddress address) {
		Set<String> motds = new HashSet<>();
		if (packet.getObject() == null)
			return motds;
		ProxyServer proxy = ProxyServer.getInstance();
		for (String server : (String[]) packet.getObject()) {
			ServerInfo serverMotd = proxy.getServerInfo(server);
			if (serverMotd != null)
				motds.add(serverMotd.getMotd());
		}
		return motds;
	}

}
