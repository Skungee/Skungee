package me.limeglass.skungee.bungeecord.bungeetablistplus;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

import codecrafter47.bungeetablistplus.api.bungee.CustomTablist;
import me.limeglass.skungee.bungeecord.handlercontroller.SkungeeBungeeHandler;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;

public class TablistColoumnsHandler extends SkungeeBungeeHandler {

	public TablistColoumnsHandler() {
		super(SkungeePacketType.BTLP_TABLISTCOLUMNS);
	}

	@Override
	public Object handlePacket(SkungeePacket packet, InetAddress address) {
		if (packet.getObject() == null)
			return null;
		CustomTablist[] tablists = (CustomTablist[]) packet.getObject();
		Set<Number> columns = new HashSet<Number>();
		for (CustomTablist tablist : tablists) {
			columns.add(tablist.getColumns());
		}
		return columns;
	}

}
