package me.limeglass.skungee.common.events;

import me.limeglass.skungee.common.player.SkungeePlayer;

public class SkungeePlayerSwitchServer extends SkungeePlayerEvent {

	public SkungeePlayerSwitchServer(String server, SkungeePlayer... players) {
		super(server, players);
	}

}
