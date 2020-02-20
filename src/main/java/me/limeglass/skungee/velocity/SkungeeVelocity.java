package me.limeglass.skungee.velocity;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import me.limeglass.skungee.EncryptionUtil;
import me.limeglass.skungee.Skungee;
import me.limeglass.skungee.common.objects.ProxyPacketResponse;
import me.limeglass.skungee.common.objects.SkungeeServer;
import me.limeglass.skungee.common.packets.ProxyPacket;
import me.limeglass.skungee.common.player.PacketPlayer;
import me.limeglass.skungee.common.player.ProxyPlayer;
import me.limeglass.skungee.common.wrappers.ProxyConfiguration;
import me.limeglass.skungee.common.wrappers.ProxyPlatform;
import me.limeglass.skungee.proxy.sockets.ServerTracker;

/**
 * Velocity
 */
@Plugin(id = "skungee", name = "Skungee", version = "@version@",
        description = "The ultimate Skript addon for Bungeecord.", authors = {"Skungee"})
public class SkungeeVelocity implements ProxyPlatform {

	private final EncryptionUtil encryption;
	private final ProxyServer server;
	private final Logger logger;

	@Inject
	public SkungeeVelocity(ProxyServer server, Logger logger) {
		encryption = new EncryptionUtil(this);
		this.server = server;
		this.logger = logger;

		logger.info("Skungee has been enabled!");
	}

	@Subscribe
	public void onProxyInitialization(ProxyInitializeEvent event) {
		try {
			Skungee.setPlatform(this);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return;
		}
		// TODO
	}

	public void consoleMessage(String string) {
		logger.info(string);
	}

	public ProxyServer getProxy() {
		return server;
	}

	@Override
	public Platform getPlatform() {
		return Platform.VELOCITY;
	}

	@Override
	public void exception(Throwable error, String... info) {
		// TODO
	}

	@Override
	public ProxyConfiguration getConfiguration() {
		return null;
	}

	@Override
	public void consoleMessage(String... messages) {
		for (String message : messages)
			logger.info(message);
	}

	@Override
	public EncryptionUtil getEncryptionUtil() {
		return encryption;
	}

	@Override
	public void debugMessage(String message) {
		logger.debug(message);
	}

	@Override
	public File getDataFolder() {
		//TODO
		return null;
	}

	@Override
	public List<ProxyPacketResponse> sendTo(ProxyPacket packet, SkungeeServer... servers) {
		return null;
	}

	@Override
	public ProxyPacketResponse send(ProxyPacket packet, SkungeeServer server) {
		return null;
	}

	@Override
	public List<ProxyPacketResponse> sendToAll(ProxyPacket... packets) {
		return null;
	}

	@Override
	public void schedule(Runnable runnable, long time, TimeUnit unit) {
		server.getScheduler()
				.buildTask(this, runnable)
				.delay(time, unit)
				.schedule();
	}

	@Override
	public ServerTracker getServerTracker() {
		return null;
	}

	@Override
	public File getScriptsFolder() {
		return null;
	}

	@Override
	public void shutdown() {
		
	}

	@Override
	public ProxyPlayer getPlayer(PacketPlayer player) {
		if (!getConfiguration().shouldAcceptIncomingUUID())
			return new VelocityPlayer(null, player.getUsername());
		return new VelocityPlayer(player);
	}

	@Override
	public void connect(SkungeeServer skungee, ProxyPlayer... players) {
		Optional<RegisteredServer> info = server.getServer(skungee.getName());
		if (!info.isPresent())
			return;
		for (ProxyPlayer player : players)
			((VelocityPlayer)player).getPlayer().ifPresent(proxied -> proxied.createConnectionRequest(info.get()).connect());
	}

	@Override
	public Set<ProxyPlayer> getPlayers() {
		return server.getAllPlayers().stream()
				.map(player -> new VelocityPlayer(player.getUniqueId(), player.getUsername()))
				.collect(Collectors.toSet());
	}

}
