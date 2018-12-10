package me.limeglass.skungee.bungeecord.handlercontroller;

import java.net.InetAddress;
import java.util.Map;

import me.limeglass.skungee.objects.packets.SkungeePacket;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

public abstract class SkungeeBungeeHandler extends SkungeeHandler {
	
	protected Map<String, ServerInfo> servers = ProxyServer.getInstance().getServers();
	
	@Override
	public Boolean onPacketCall(SkungeePacket packet, InetAddress address) {
		return true;
	}

}
