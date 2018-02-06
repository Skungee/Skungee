package me.limeglass.skungee.objects.events;

import me.limeglass.skungee.objects.SkungeePlayer;

public class PlayerSwitchServerEvent extends BungeecordEvent {

	public PlayerSwitchServerEvent(String server, SkungeePlayer[] players) {
		super(server, players);
	}
}