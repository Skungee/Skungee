package me.limeglass.skungee.objects.events;

import me.limeglass.skungee.objects.SkungeePlayer;

public class SkungeePlayerDisconnect extends SkungeePlayerEvent {

	public SkungeePlayerDisconnect(String server, SkungeePlayer... players) {
		super(server, players);
	}

}
