package me.limeglass.skungee.objects.events;

import me.limeglass.skungee.objects.SkungeePlayer;

public class PlayerDisconnectEvent extends BungeecordEvent {

	public PlayerDisconnectEvent(String server, SkungeePlayer... players) {
		super(server, players);
	}
}