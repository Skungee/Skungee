package me.limeglass.skungee.proxy.handlers.bungeetablistplus;

import java.net.InetAddress;

import codecrafter47.bungeetablistplus.api.bungee.CustomTablist;
import me.limeglass.skungee.common.handlercontroller.SkungeeBungeePlayerHandler;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.common.wrappers.SkungeePlatform.Platform;

public class PlayerTablistHandler extends SkungeeBungeePlayerHandler {

	public PlayerTablistHandler() {
		super(Platform.BUNGEECORD, ServerPacketType.BTLP_PLAYERTABLIST);
	}

	@Override
	public Object handlePacket(ServerPacket packet, InetAddress address) {
		if (packet.getChangeMode() == null)
			return BungeeTabListPlusManager.getTablist(players);
		Object object = packet.getObject();
		switch (packet.getChangeMode()) {
			case SET:
				if (object == null) return null;
				Object[] setChanger = (Object[]) object;
				if (!(setChanger[0] instanceof CustomTablist))
					return null;
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
