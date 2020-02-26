package me.limeglass.skungee.velocity;

import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.UUID;

import org.eclipse.jdt.annotation.Nullable;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.util.MessagePosition;

import me.limeglass.skungee.Skungee;
import me.limeglass.skungee.common.player.PacketPlayer;
import me.limeglass.skungee.common.player.ProxyPlayer;
import net.kyori.text.ComponentBuilders;

public class VelocityPlayer implements ProxyPlayer {

	private static final long serialVersionUID = 4047363530550129968L;
	private final String username;
	@Nullable
	private final UUID uuid;

	public VelocityPlayer(PacketPlayer player) {
		this.username = player.getUsername();
		this.uuid = player.getUUID();
	}

	public VelocityPlayer(@Nullable UUID uuid, String username) {
		this.username = username;
		this.uuid = uuid;
	}

	public Optional<Player> getPlayer() {
		SkungeeVelocity skungee = (SkungeeVelocity) Skungee.getPlatform();
		ProxyServer proxy = skungee.getProxy();
		return uuid == null ? proxy.getPlayer(username) : proxy.getPlayer(uuid);
	}

	@Override
	public void sendMessage(String... messages) {
		Optional<Player> player = getPlayer();
		if (!player.isPresent())
			return;
		for (String message : messages)
			player.get().sendMessage(ComponentBuilders.text(message).build());
	}

	public void sendActionbar(String message) {
		getPlayer().ifPresent(player -> player.sendMessage(ComponentBuilders.translatable(message).build(), MessagePosition.ACTION_BAR));
	}

	@Override
	public void disconnect(String message) {
		getPlayer().ifPresent(player -> player.disconnect(ComponentBuilders.text(message).build()));
	}

	@Override
	public InetSocketAddress getAddress() {
		Optional<Player> player = getPlayer();
		if (!player.isPresent())
			return null;
		return player.get().getRemoteAddress();
	}

	@Override
	public void chat(String message) {
		getPlayer().ifPresent(player -> player.spoofChatInput(message));
	}

	@Override
	public String getServerName() {
		Optional<Player> player = getPlayer();
		if (!player.isPresent())
			return null;
		// Because why is this a optional velocity? Are they not on the proxy at all or...?
		Optional<ServerConnection> server = player.get().getCurrentServer();
		if (!server.isPresent())
			return null;
		return server.get().getServerInfo().getName();
	}

	@Override
	public byte getViewDistance() {
		Optional<Player> player = getPlayer();
		if (!player.isPresent())
			return -1;
		return player.get().getPlayerSettings().getViewDistance();
	}

	@Override
	public long getPing() {
		Optional<Player> player = getPlayer();
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

	@Override
	public boolean isConnected() {
		Optional<Player> player = getPlayer();
		if (!player.isPresent())
			return false;
		return player.get().isActive();
	}

}
