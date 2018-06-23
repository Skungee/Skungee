package me.limeglass.skungee.bungeecord.handlercontroller;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Map;
import me.limeglass.skungee.objects.SkungeePacket;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

public abstract class SkungeeBungeeHandler extends SkungeeHandler implements Serializable {
	
	private static final long serialVersionUID = 5368355604794000483L;
	protected Map<String, ServerInfo> servers = ProxyServer.getInstance().getServers();
	
	public abstract Object handlePacket(SkungeePacket packet, InetAddress address);
	
	public void onPacketCall(SkungeePacket packet, InetAddress address) {}
}