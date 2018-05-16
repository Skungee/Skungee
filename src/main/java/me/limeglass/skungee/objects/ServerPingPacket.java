package me.limeglass.skungee.objects;

public class ServerPingPacket extends BungeePacket {

	private static final long serialVersionUID = 1374798819485573682L;
	private String description, version, favicon;
	//private Players players;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getFavicon() {
		return favicon;
	}

	public void setFavicon(String favicon) {
		this.favicon = favicon;
	}
}