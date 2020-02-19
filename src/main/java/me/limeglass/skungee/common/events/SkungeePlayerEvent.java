package me.limeglass.skungee.common.events;

import me.limeglass.skungee.bungeecord.events.BungeecordEvent;
import me.limeglass.skungee.common.objects.Returnable;
import me.limeglass.skungee.common.player.SkungeePlayer;

public class SkungeePlayerEvent extends BungeecordEvent implements Returnable {

	private final SkungeePlayer[] players;
	
	public SkungeePlayerEvent(String server, SkungeePlayer... players) {
		super(server);
		this.players = players;
	}

	public SkungeePlayer[] getPlayers() {
		return players;
	}
	
	public Object[] getConverted() {
		return convert(players);
	}
	
}
