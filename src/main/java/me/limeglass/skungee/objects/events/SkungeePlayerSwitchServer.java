package me.limeglass.skungee.objects.events;

import me.limeglass.skungee.objects.SkungeePlayer;

public class SkungeePlayerSwitchServer extends BungeecordEvent {

	public SkungeePlayerSwitchServer(String server, SkungeePlayer player) {
		super(server, player);
	}
}