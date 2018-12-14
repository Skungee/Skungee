package me.limeglass.skungee.objects.events;

import me.limeglass.skungee.objects.Returnable;
import me.limeglass.skungee.objects.SkungeePlayer;

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
