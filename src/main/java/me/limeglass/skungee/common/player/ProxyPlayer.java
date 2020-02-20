package me.limeglass.skungee.common.player;

import java.net.InetSocketAddress;

public interface ProxyPlayer extends SkungeePlayer {

	public void sendMessage(String... messages);

	public void sendActionbar(String message);

	public InetSocketAddress getAddress();

	public void chat(String message);

	public String getServerName();

	public byte getViewDistance();

	public long getPing();

}
