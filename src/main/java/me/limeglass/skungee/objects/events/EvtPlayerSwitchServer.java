package me.limeglass.skungee.objects.events;

import me.limeglass.skungee.objects.SkungeePlayer;

public class EvtPlayerSwitchServer extends BungeecordEvent {

	public EvtPlayerSwitchServer(String server, SkungeePlayer player) {
		super(server, player);
	}
}