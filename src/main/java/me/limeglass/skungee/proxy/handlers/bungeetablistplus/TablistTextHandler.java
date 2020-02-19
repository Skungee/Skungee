package me.limeglass.skungee.proxy.handlers.bungeetablistplus;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import codecrafter47.bungeetablistplus.api.bungee.CustomTablist;
import me.limeglass.skungee.common.handlercontroller.SkungeeProxyHandler;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.common.wrappers.SkungeePlatform.Platform;

public class TablistTextHandler extends SkungeeProxyHandler {

	public TablistTextHandler() {
		super(Platform.BUNGEECORD, ServerPacketType.BTLP_TABLISTTEXT);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object handlePacket(ServerPacket packet, InetAddress address) {
		if (packet.getObject() == null || packet.getSetObject() == null)
			return null;
		CustomTablist[] tablists = (CustomTablist[]) packet.getObject();
		List<Integer> slots = (ArrayList<Integer>) packet.getSetObject();
		if (tablists.length <= 0 || slots.isEmpty()) return null;
		Set<String> text = new HashSet<String>();
		for (CustomTablist tablist : tablists) {
			text.add(tablist.getText((int)slots.get(0), (int)slots.get(1)));
		}
		return text;
	}

}
