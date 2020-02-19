package me.limeglass.skungee.common.player;

import java.util.UUID;

/**
 * A basic player wrapper that can be sent over the protocol.
 */
public class PacketPlayer implements SkungeePlayer {

	private static final long serialVersionUID = -121162976280176396L;
	private final String username;
	private final UUID uuid;

	public PacketPlayer(UUID uuid, String username) {
		this.username = username;
		this.uuid = uuid;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public UUID getUUID() {
		return uuid;
	}

}
