package me.limeglass.skungee.proxy.handlers.returnables;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import me.limeglass.skungee.bungeecord.BungeePlayer;
import me.limeglass.skungee.common.handlercontroller.SkungeeProxyHandler;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.common.wrappers.SkungeePlatform.Platform;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PlayerDisplayNameHandler extends SkungeeProxyHandler<Set<String>> {

	public PlayerDisplayNameHandler() {
		super(Platform.BUNGEECORD, ServerPacketType.PLAYERDISPLAYNAME);
	}

	@Override
	public Set<String> handlePacket(ServerPacket packet, InetAddress address) {
		Set<ProxiedPlayer> players =  proxy.getPlayers(packet.getPlayers()).stream()
				.map(player -> ((BungeePlayer)player).getPlayer())
				.filter(optional -> optional.isPresent())
				.map(optional -> optional.get())
				.collect(Collectors.toSet());
		Set<String> names = new HashSet<String>();
		if (!players.isEmpty()) {
			for (ProxiedPlayer player : players) {
				names.add(player.getDisplayName());
				if (packet.getObject() != null && packet.getChangeMode() != null) {
					switch (packet.getChangeMode()) {
						case SET:
						case ADD:
							player.setDisplayName((String) packet.getObject());
							break;
						case DELETE:
						case REMOVE:
						case REMOVE_ALL:
						case RESET:
							player.setDisplayName((String) packet.getObject());
							break;
					}
				}
			}
			return names;
		}
		return names;
	}

}
