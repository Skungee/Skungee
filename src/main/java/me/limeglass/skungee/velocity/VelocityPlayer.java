package me.limeglass.skungee.velocity;

import java.util.Optional;
import java.util.UUID;

import org.eclipse.jdt.annotation.Nullable;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
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

	public void sendActionbar(String message) {
		getPlayer().ifPresent(player -> player.sendMessage(ComponentBuilders.translatable(message).build(), MessagePosition.ACTION_BAR));
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
