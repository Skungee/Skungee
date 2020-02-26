package me.limeglass.skungee.common.player;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.UUID;

public interface SkungeePlayer extends Serializable {

	public void sendMessage(String... messages);

	public InetSocketAddress getAddress();

	public boolean isConnected();

	public String getUsername();

	public UUID getUUID();

}
