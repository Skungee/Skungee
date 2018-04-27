package me.limeglass.skungee.objects.events;

import me.limeglass.skungee.objects.SkungeePlayer;

public class EvtPlayerDisconnect extends BungeecordEvent {

	public EvtPlayerDisconnect(String server, SkungeePlayer player) {
		super(server, player);
	}
}