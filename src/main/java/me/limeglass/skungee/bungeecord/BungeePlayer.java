package me.limeglass.skungee.bungeecord;

import java.util.Optional;
import java.util.UUID;

import org.eclipse.jdt.annotation.Nullable;

import me.limeglass.skungee.common.player.PacketPlayer;
import me.limeglass.skungee.common.player.ProxyPlayer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeePlayer implements ProxyPlayer {

	private static final long serialVersionUID = 4047363530550129968L;
	private final String username;
	@Nullable
	private final UUID uuid;

	public BungeePlayer(PacketPlayer player) {
		this.username = player.getUsername();
		this.uuid = player.getUUID();
	}

	public BungeePlayer(@Nullable UUID uuid, String username) {
		this.username = username;
		this.uuid = uuid;
	}

	public Optional<ProxiedPlayer> getPlayer() {
		ProxyServer server = ProxyServer.getInstance();
		return Optional.ofNullable(uuid == null ? server.getPlayer(username) : server.getPlayer(uuid));
	}

	public void sendActionbar(String message) {
		getPlayer().ifPresent(player -> player.sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message)));
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public UUID getUUID() {
		return uuid;
	}

	@Override
	public String toString() {
		if (uuid == null)
			return username;
		return uuid + ":" + username;
	}

}
