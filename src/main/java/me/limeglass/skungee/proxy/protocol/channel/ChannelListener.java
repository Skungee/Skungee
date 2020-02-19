package me.limeglass.skungee.proxy.protocol.channel;

import java.util.Optional;

import me.limeglass.skungee.proxy.protocol.ProtocolPlayer;
import me.limeglass.skungee.proxy.protocol.ProtocolPlayerManager;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class ChannelListener implements Listener {
	
	//TODO don't forget to register.
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPostLogin(PostLoginEvent event) {
		ProtocolPlayer player = new ProtocolPlayer(event.getPlayer().getPendingConnection().getVersion(), event.getPlayer().getUniqueId());
		ChannelManager.addChannel(player, event.getPlayer());
		ProtocolPlayerManager.addPlayer(player);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerDisconnect(PlayerDisconnectEvent event) {
		ProtocolPlayerManager.removePlayer(event.getPlayer().getUniqueId());
		ChannelManager.removeChannel(event.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onServerConnected(ServerConnectedEvent event) {
		Optional<ProtocolPlayer> player = ProtocolPlayerManager.getPlayer(event.getPlayer().getUniqueId());
		if (player.isPresent()) player.get().setServer(event.getServer().getInfo().getName());
	}

}