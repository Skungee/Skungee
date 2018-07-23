package me.limeglass.skungee.bungeecord.bungeetablistplus;

import java.net.InetAddress;

import codecrafter47.bungeetablistplus.api.bungee.CustomTablist;
import me.limeglass.skungee.bungeecord.handlercontroller.SkungeePlayerHandler;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;

public class PlayerTablistHandler extends SkungeePlayerHandler {
	
	//BungeeTabListPlus
	
	static {
		registerPacket(new PlayerTablistHandler(), SkungeePacketType.BTLP_PLAYERTABLIST);
	}

	@Override
	public Object handlePacket(SkungeePacket packet, InetAddress address) {
		if (packet.getChangeMode() == null)	return BungeeTabListPlusManager.getTablist(players);
		Object object = packet.getObject();
		switch (packet.getChangeMode()) {
			case SET:
				if (object == null) return null;
				Object[] setChanger = (Object[]) object;
				if (!(setChanger[0] instanceof CustomTablist)) return null;
				BungeeTabListPlusManager.setTablist(players, (CustomTablist) setChanger[0]);
				break;
			case RESET:
			case DELETE:
				BungeeTabListPlusManager.removeTablist(players);
				break;
			case REMOVE:
			case REMOVE_ALL:
			case ADD:
			default:
				break;
		}
		return null;
	}
}
