package me.limeglass.skungee.proxy.handlers.bungeetablistplus;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

import codecrafter47.bungeetablistplus.api.bungee.CustomTablist;
import me.limeglass.skungee.common.handlercontroller.SkungeeProxyHandler;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.common.wrappers.SkungeePlatform.Platform;

public class TablistHeaderHandler extends SkungeeProxyHandler<Set<String>> {

	public TablistHeaderHandler() {
		super(Platform.BUNGEECORD, ServerPacketType.BTLP_TABLISTHEADER);
	}

	@Override
	public Set<String> handlePacket(ServerPacket packet, InetAddress address) {
		if (packet.getObject() == null)
			return null;
		CustomTablist[] tablists = (CustomTablist[]) packet.getObject();
		Set<String> headers = new HashSet<String>();
		for (CustomTablist tablist : tablists) {
			headers.add(tablist.getHeader());
		}
		return headers;
	}

}
