package me.limeglass.skungee.common.events;

import me.limeglass.skungee.bungeecord.events.BungeecordEvent;
import me.limeglass.skungee.common.objects.Returnable;
import me.limeglass.skungee.common.player.PacketPlayer;

public class SkungeePlayerEvent extends BungeecordEvent implements Returnable {

	private final PacketPlayer[] players;

	public SkungeePlayerEvent(String server, PacketPlayer... players) {
		super(server);
		this.players = players;
	}

	public PacketPlayer[] getPlayers() {
		return players;
	}

	public Object[] getConverted() {
		return convert(players);
	}

}
