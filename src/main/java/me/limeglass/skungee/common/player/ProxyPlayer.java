package me.limeglass.skungee.common.player;

public interface ProxyPlayer extends SkungeePlayer {

	public void sendActionbar(String message);

	public void disconnect(String message);

	public void chat(String message);

	public String getServerName();

	public byte getViewDistance();

	public long getPing();

}
