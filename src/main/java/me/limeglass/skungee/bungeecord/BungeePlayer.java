package me.limeglass.skungee.bungeecord;

import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.UUID;

import org.eclipse.jdt.annotation.Nullable;

import me.limeglass.skungee.common.player.PacketPlayer;
import me.limeglass.skungee.common.player.ProxyPlayer;
import net.md_5.bungee.api.ChatColor;
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

	@Override
	public void sendMessage(String... messages) {
		Optional<ProxiedPlayer> player = getPlayer();
		if (!player.isPresent())
			return;
		for (String message : messages)
			player.get().sendMessage(TextComponent.fromLegacyText(message));
	}

	public void sendActionbar(String message) {
		getPlayer().ifPresent(player -> player.sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message)));
	}

	@Override
	public void disconnect(String message) {
		getPlayer().ifPresent(player -> player.disconnect(new TextComponent(message)));
	}

	@SuppressWarnings("deprecation")
	@Override
	public InetSocketAddress getAddress() {
		Optional<ProxiedPlayer> player = getPlayer();
		if (!player.isPresent())
			return null;
		return player.get().getAddress();
	}

	@Override
	public void chat(String message) {
		getPlayer().ifPresent(player -> player.chat(ChatColor.stripColor(message)));
	}

	@Override
	public byte getViewDistance() {
		Optional<ProxiedPlayer> player = getPlayer();
		if (!player.isPresent())
			return -1;
		return player.get().getViewDistance();
	}

	@Override
	public String getServerName() {
		Optional<ProxiedPlayer> player = getPlayer();
		if (!player.isPresent())
			return null;
		return player.get().getServer().getInfo().getName();
	}

	@Override
	public boolean isConnected() {
		Optional<ProxiedPlayer> player = getPlayer();
		if (!player.isPresent())
			return false;
		return player.get().isConnected();
	}

	@Override
	public long getPing() {
		Optional<ProxiedPlayer> player = getPlayer();
		if (!player.isPresent())
			return -1;
		return player.get().getPing();
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
