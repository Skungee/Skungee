package me.limeglass.skungee.bungeecord.bungeetablistplus;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import codecrafter47.bungeetablistplus.api.bungee.CustomTablist;
import me.limeglass.skungee.bungeecord.handlercontroller.SkungeeBungeeHandler;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;

public class TablistTextHandler extends SkungeeBungeeHandler {

	public TablistTextHandler() {
		super(SkungeePacketType.BTLP_TABLISTTEXT);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object handlePacket(SkungeePacket packet, InetAddress address) {
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
