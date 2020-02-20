package me.limeglass.skungee.proxy.handlers.bungeetablistplus;

import java.net.InetAddress;
import java.util.Set;
import java.util.stream.Collectors;

import codecrafter47.bungeetablistplus.api.bungee.CustomTablist;
import me.limeglass.skungee.bungeecord.BungeePlayer;
import me.limeglass.skungee.common.handlercontroller.SkungeeProxyHandler;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.common.wrappers.ProxyPlatform;
import me.limeglass.skungee.common.wrappers.SkungeePlatform.Platform;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PlayerTablistHandler extends SkungeeProxyHandler<Set<CustomTablist>> {

	public PlayerTablistHandler() {
		super(Platform.BUNGEECORD, ServerPacketType.BTLP_PLAYERTABLIST);
	}

	@Override
	public Set<CustomTablist> handlePacket(ServerPacket packet, InetAddress address) {
		Set<ProxiedPlayer> players = ((ProxyPlatform)platform).getPlayers(packet.getPlayers()).stream()
				.map(player -> ((BungeePlayer)player).getPlayer())
				.filter(optional -> optional.isPresent())
				.map(optional -> optional.get())
				.collect(Collectors.toSet());
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
