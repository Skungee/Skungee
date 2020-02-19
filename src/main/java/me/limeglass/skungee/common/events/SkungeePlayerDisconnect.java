package me.limeglass.skungee.common.events;

import me.limeglass.skungee.common.player.SkungeePlayer;

public class SkungeePlayerDisconnect extends SkungeePlayerEvent {

	public SkungeePlayerDisconnect(String server, SkungeePlayer... players) {
		super(server, players);
	}

}
