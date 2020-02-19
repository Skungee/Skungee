package me.limeglass.skungee.proxy.handlers.returnables;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

import me.limeglass.skungee.bungeecord.SkungeeBungee;
import me.limeglass.skungee.bungeecord.managers.PlayerTimeManager;
import me.limeglass.skungee.bungeecord.managers.PlayerTimeManager.PlayerTime;
import me.limeglass.skungee.common.handlercontroller.SkungeeBungeePlayerHandler;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PlayerPlayTimeHandler extends SkungeeBungeePlayerHandler {

	private final PlayerTimeManager playerTimeManager;

	public PlayerPlayTimeHandler() {
		super(ServerPacketType.PLAYTIME);
		playerTimeManager = SkungeeBungee.getInstance().getPlayerTimeManager();
	}

	@Override
	public Object handlePacket(ServerPacket packet, InetAddress address) {
		Object object = packet.getObject();
		Set<Integer> times = new HashSet<>();
		for (ProxiedPlayer player : players) {
			PlayerTime time = playerTimeManager.getPlayerTime(player);
			if (object == null)
				return times.add(time.getTotal());
			return times.addAll(time.getSeconds((String[])object));
		}
		return times;
	}

}
