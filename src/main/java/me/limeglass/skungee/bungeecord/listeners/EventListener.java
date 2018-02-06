package me.limeglass.skungee.bungeecord.listeners;

import me.limeglass.skungee.bungeecord.sockets.BungeeSockets;
import me.limeglass.skungee.bungeecord.sockets.ServerTracker;
import me.limeglass.skungee.objects.BungeePacket;
import me.limeglass.skungee.objects.BungeePacketType;
import me.limeglass.skungee.objects.ConnectedServer;
import me.limeglass.skungee.objects.SkungeePlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class EventListener implements Listener {
	
	@EventHandler
    public void onServerSwitch(ServerSwitchEvent event) {
		if (event.getPlayer() != null) {
			SkungeePlayer player = new SkungeePlayer(false, event.getPlayer().getUniqueId(), event.getPlayer().getName());
			if (event.getPlayer().getServer() != null) {
				BungeePacket packet = new BungeePacket(true, BungeePacketType.PLAYERSWITCH, event.getPlayer().getServer().getInfo().getName(), player);
				BungeeSockets.sendAll(packet);
			}
		}
    }
	
	@EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
		if (event.getPlayer() != null) {
			SkungeePlayer player = new SkungeePlayer(false, event.getPlayer().getUniqueId(), event.getPlayer().getName());
			if (event.getPlayer().getServer() != null) {
				ConnectedServer server = ServerTracker.get(event.getPlayer().getServer().getInfo().getName());
				BungeePacket packet = new BungeePacket(true, BungeePacketType.PLAYERDISCONNECT, server.getName(), player);
				BungeeSockets.sendAll(packet);
			}
		}
    }
}