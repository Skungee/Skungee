package me.limeglass.skungee.objects.events;

import me.limeglass.skungee.objects.SkungeePlayer;

public class SkungeePlayerSwitchServer extends SkungeePlayerEvent {

	public SkungeePlayerSwitchServer(String server, SkungeePlayer... players) {
		super(server, players);
	}

}
