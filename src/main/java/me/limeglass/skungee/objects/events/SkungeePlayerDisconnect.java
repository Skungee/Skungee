package me.limeglass.skungee.objects.events;

import me.limeglass.skungee.objects.SkungeePlayer;

public class SkungeePlayerDisconnect extends BungeecordEvent {

	public SkungeePlayerDisconnect(String server, SkungeePlayer player) {
		super(server, player);
	}
}