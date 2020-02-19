package me.limeglass.skungee.proxy.handlers.bungeetablistplus;

import java.net.InetAddress;

import codecrafter47.bungeetablistplus.api.bungee.BungeeTabListPlusAPI;
import codecrafter47.bungeetablistplus.api.bungee.CustomTablist;
import me.limeglass.skungee.common.handlercontroller.SkungeeProxyHandler;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.common.wrappers.SkungeePlatform.Platform;

public class TablistHandler extends SkungeeProxyHandler {

	public TablistHandler() {
		super(Platform.BUNGEECORD, ServerPacketType.BTLP_TABLIST);
	}

	@Override
	public Object handlePacket(ServerPacket packet, InetAddress address) {
		CustomTablist tablist = BungeeTabListPlusAPI.createCustomTablist();
		Object size = packet.getObject();
		if (size != null)
			tablist.setSize((int) size);
		else
			tablist.setSize(80);
		return new CustomTablist[] {tablist};
	}

}
