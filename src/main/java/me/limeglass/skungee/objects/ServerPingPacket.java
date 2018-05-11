package me.limeglass.skungee.objects;

import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ServerPing.Players;
import net.md_5.bungee.api.ServerPing.Protocol;
import net.md_5.bungee.api.chat.BaseComponent;

public class ServerPingPacket extends BungeePacket {

	private static final long serialVersionUID = 1374798819485573682L;
	private BaseComponent description;
	private Protocol version;
	private Favicon favicon;
	private Players players;

	public ServerPingPacket(Boolean returnable, BungeePacketType type, Object object) {
		super(returnable, type, object);
	}
	
	public ServerPingPacket(Boolean returnable, BungeePacketType type, SkungeePlayer... players) {
		super(returnable, type, players);
	}
	
	public ServerPingPacket(Boolean returnable, BungeePacketType type, Object object, Object settable) {
		super(returnable, type, object, settable);
	}
	
	public ServerPingPacket(Boolean returnable, BungeePacketType type, Object object, SkungeePlayer... players) {
		super(returnable, type, object, players);
	}
	
	public ServerPingPacket(Boolean returnable, BungeePacketType type, Object object, Object settable, SkungeePlayer... players) {
		super(returnable, type, object, settable, players);
	}

	public BaseComponent getDescription() {
		return description;
	}

	public void setDescription(BaseComponent description) {
		this.description = description;
	}

	public Protocol getVersion() {
		return version;
	}

	public void setVersion(Protocol version) {
		this.version = version;
	}

	public Favicon getFavicon() {
		return favicon;
	}

	public void setFavicon(Favicon favicon) {
		this.favicon = favicon;
	}

	public Players getPingPlayers() {
		return players;
	}

	public void setPingPlayers(Players players) {
		this.players = players;
	}
}