package me.limeglass.skungee.common.wrappers;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import me.limeglass.skungee.common.objects.ProxyPacketResponse;
import me.limeglass.skungee.common.objects.SkungeeServer;
import me.limeglass.skungee.common.packets.ProxyPacket;
import me.limeglass.skungee.common.player.PacketPlayer;
import me.limeglass.skungee.common.player.ProxyPlayer;
import me.limeglass.skungee.proxy.sockets.ServerTracker;

public interface ProxyPlatform extends SkungeePlatform {

	public List<ProxyPacketResponse> sendTo(ProxyPacket packet, SkungeeServer... servers);

	public ProxyPacketResponse send(ProxyPacket packet, SkungeeServer server);

	public List<ProxyPacketResponse> sendToAll(ProxyPacket... packets);

	public void schedule(Runnable runnable, long time, TimeUnit unit);

	public default void connect(SkungeeServer server, Collection<ProxyPlayer> players) {
		players.forEach(player -> connect(server, player));
	}

	public void connect(SkungeeServer server, ProxyPlayer... players);

	public ProxyPlayer getPlayer(PacketPlayer player);

	public ProxyPlayer getPlayer(UUID uuid);

	public default Set<ProxyPlayer> getPlayers(PacketPlayer... players) {
		if (players == null)
			return new HashSet<>();
		return Arrays.stream(players)
				.map(player -> getPlayer(player))
				.collect(Collectors.toSet());
	}

	@Override
	public ProxyConfiguration getConfiguration();

	public ServerTracker getServerTracker();

	public Set<ProxyPlayer> getPlayers();

	public File getScriptsFolder();

	public void shutdown();

}
