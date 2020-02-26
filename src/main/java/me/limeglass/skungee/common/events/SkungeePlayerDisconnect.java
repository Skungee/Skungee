package me.limeglass.skungee.common.events;

import me.limeglass.skungee.common.player.PacketPlayer;

public class SkungeePlayerDisconnect extends SkungeePlayerEvent {

	public SkungeePlayerDisconnect(String server, PacketPlayer... players) {
		super(server, players);
	}

}
