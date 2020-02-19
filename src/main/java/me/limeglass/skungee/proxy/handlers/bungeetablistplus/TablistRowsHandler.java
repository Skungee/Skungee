package me.limeglass.skungee.proxy.handlers.bungeetablistplus;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

import codecrafter47.bungeetablistplus.api.bungee.CustomTablist;
import me.limeglass.skungee.common.handlercontroller.SkungeeProxyHandler;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.common.wrappers.SkungeePlatform.Platform;

public class TablistRowsHandler extends SkungeeProxyHandler {

	public TablistRowsHandler() {
		super(Platform.BUNGEECORD, ServerPacketType.BTLP_TABLISTROWS);
	}

	@Override
	public Object handlePacket(ServerPacket packet, InetAddress address) {
		if (packet.getObject() == null)
			return null;
		CustomTablist[] tablists = (CustomTablist[]) packet.getObject();
		Set<Number> rows = new HashSet<Number>();
		for (CustomTablist tablist : tablists) {
			rows.add(tablist.getRows());
		}
		return rows;
	}

}
