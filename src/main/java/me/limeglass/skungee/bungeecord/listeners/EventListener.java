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
	
	//private EncryptionUtil serializer = Skungee.getEncrypter();
	
	@EventHandler
    public void onServerSwitch(ServerSwitchEvent event) {
		if (event.getPlayer() != null) {
			SkungeePlayer player = new SkungeePlayer(false, event.getPlayer().getUniqueId(), event.getPlayer().getName());
			if (event.getPlayer().getServer() != null) {
				BungeePacket packet = new BungeePacket(false, BungeePacketType.PLAYERSWITCH, event.getPlayer().getServer().getInfo().getName(), null, player);
				BungeeSockets.sendAll(packet);
			}
		}
    }
	
	@EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
		if (event.getPlayer() != null) {
			SkungeePlayer player = new SkungeePlayer(false, event.getPlayer().getUniqueId(), event.getPlayer().getName());
			if (event.getPlayer().getServer() != null) {
				ConnectedServer[] servers = ServerTracker.get(event.getPlayer().getServer().getInfo().getName());
				if (servers == null) return; //Not a valid Skungee connected server
				BungeePacket packet = new BungeePacket(false, BungeePacketType.PLAYERDISCONNECT, servers[0].getName(), null, player);
				BungeeSockets.sendAll(packet);
			}
		}
    }
	
	/*@EventHandler
    public void onPing(ProxyPingEvent event) {
		if (event.getResponse() != null) {
			ServerPing ping = event.getResponse();
			//List<Object> values = Arrays.asList(new Object[]{ping.getDescriptionComponent(), ping.getFaviconObject(), ping.getPlayers().getMax(), ping.getModinfo()});
			//This will return all the modified ping event-values from all the scripts on every server.
			//If a script wants the motd to be something but another server wants differently, this will default to the last server as that's not possible. The Bungeecord only has one motd.
			//Only one script should handle modifying the Bungeecord motd. If it's a global script and all the values are the same, Skungee will handle that.
			ServerPingPacket send = new ServerPingPacket(true, BungeePacketType.SERVERLISTPING);
			send.setDescription(serializer.serialize(ping.getDescriptionComponent()));
			send.setFavicon(serializer.serialize(ping.getFaviconObject()));
			send.setPingPlayers(serializer.serialize(ping.getPlayers()));
			for (Object packet : BungeeSockets.sendAll(send)) {
				ServerPingPacket returned = (ServerPingPacket) packet;
				if (returned.getDescription() != null) ping.setDescriptionComponent((BaseComponent) serializer.deserialize(returned.getDescription()));
				if (returned.getPingPlayers() != null) ping.setPlayers((Players) serializer.deserialize(returned.getPingPlayers()));
				if (returned.getFavicon() != null) ping.setFavicon((Favicon) serializer.deserialize(returned.getFavicon()));
				if (returned.getVersion() != null) ping.setVersion((Protocol) serializer.deserialize(returned.getVersion()));
			}
			event.setResponse(ping);
		}
    }*/
}