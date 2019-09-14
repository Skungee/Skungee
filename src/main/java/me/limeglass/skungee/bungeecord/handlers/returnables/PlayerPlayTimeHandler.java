package me.limeglass.skungee.bungeecord.handlers.returnables;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

import me.limeglass.skungee.bungeecord.Skungee;
import me.limeglass.skungee.bungeecord.handlercontroller.SkungeePlayerHandler;
import me.limeglass.skungee.bungeecord.managers.PlayerTimeManager;
import me.limeglass.skungee.bungeecord.managers.PlayerTimeManager.PlayerTime;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PlayerPlayTimeHandler extends SkungeePlayerHandler {

	private final PlayerTimeManager playerTimeManager;

	public PlayerPlayTimeHandler() {
		super(SkungeePacketType.PLAYTIME);
		playerTimeManager = Skungee.getInstance().getPlayerTimeManager();
	}

	@Override
	public Object handlePacket(SkungeePacket packet, InetAddress address) {
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
