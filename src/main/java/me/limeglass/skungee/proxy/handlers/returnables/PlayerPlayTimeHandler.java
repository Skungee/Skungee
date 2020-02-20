package me.limeglass.skungee.proxy.handlers.returnables;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import me.limeglass.skungee.bungeecord.BungeePlayer;
import me.limeglass.skungee.bungeecord.SkungeeBungee;
import me.limeglass.skungee.bungeecord.managers.PlayerTimeManager;
import me.limeglass.skungee.bungeecord.managers.PlayerTimeManager.PlayerTime;
import me.limeglass.skungee.common.handlercontroller.SkungeeProxyHandler;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.common.wrappers.SkungeePlatform.Platform;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PlayerPlayTimeHandler extends SkungeeProxyHandler<Set<Integer>> {

	private final PlayerTimeManager playerTimeManager;

	public PlayerPlayTimeHandler() {
		super(Platform.BUNGEECORD, ServerPacketType.PLAYTIME);
		playerTimeManager = ((SkungeeBungee)platform).getPlayerTimeManager();
	}

	@Override
	public Set<Integer> handlePacket(ServerPacket packet, InetAddress address) {
		Object object = packet.getObject();
		Set<Integer> times = new HashSet<>();
		Set<ProxiedPlayer> players = proxy.getPlayers(packet.getPlayers()).stream()
				.map(player -> ((BungeePlayer)player).getPlayer())
				.filter(optional -> optional.isPresent())
				.map(optional -> optional.get())
				.collect(Collectors.toSet());
		for (ProxiedPlayer player : players) {
			PlayerTime time = playerTimeManager.getPlayerTime(player);
			if (object == null) {
				times.add(time.getTotal());
				continue;
			}
			times.addAll(time.getSeconds((String[])object));
		}
		return times;
	}

}
