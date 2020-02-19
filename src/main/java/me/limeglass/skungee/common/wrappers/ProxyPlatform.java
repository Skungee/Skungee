package me.limeglass.skungee.common.wrappers;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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

	public Set<ProxyPlayer> getPlayers(PacketPlayer... player);

	public Optional<ProxyPlayer> getPlayer(PacketPlayer player);

	@Override
	public ProxyConfiguration getConfiguration();

	public ServerTracker getServerTracker();

	public File getScriptsFolder();

	public void shutdown();

}
