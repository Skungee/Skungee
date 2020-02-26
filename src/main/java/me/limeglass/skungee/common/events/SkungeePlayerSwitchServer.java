package me.limeglass.skungee.common.events;

import me.limeglass.skungee.common.player.PacketPlayer;

public class SkungeePlayerSwitchServer extends SkungeePlayerEvent {

	public SkungeePlayerSwitchServer(String server, PacketPlayer... players) {
		super(server, players);
	}

}
