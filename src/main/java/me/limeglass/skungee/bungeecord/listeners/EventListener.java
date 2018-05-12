package me.limeglass.skungee.bungeecord.listeners;

import java.util.regex.Pattern;

import me.limeglass.skungee.bungeecord.Skungee;
import me.limeglass.skungee.bungeecord.sockets.BungeeSockets;
import me.limeglass.skungee.bungeecord.sockets.ServerTracker;
import me.limeglass.skungee.objects.BungeePacket;
import me.limeglass.skungee.objects.BungeePacketType;
import me.limeglass.skungee.objects.ConnectedServer;
import me.limeglass.skungee.objects.ServerPingPacket;
import me.limeglass.skungee.objects.SkungeePlayer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.ServerPing.Protocol;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.protocol.ProtocolConstants;

public class EventListener implements Listener {
	
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
	
	@EventHandler
    public void onPing(ProxyPingEvent event) {
		if (event.getResponse() != null) {
			ServerPing ping = event.getResponse();
			//This will return all the modified ping event-values from all the scripts on every server.
			//If a script wants the motd to be something but another server wants differently, this will default to the last server as that's not possible. The Bungeecord only has one motd.
			//Only one script should handle modifying the Bungeecord motd. If it's a global script and all the values are the same, Skungee will handle that.
			ServerPingPacket packet = new ServerPingPacket(true, BungeePacketType.SERVERLISTPING, ProtocolConstants.SUPPORTED_VERSION_IDS);
			packet.setVersion(ping.getVersion().getName() + ":" + ping.getVersion().getProtocol());
			packet.setDescription(ping.getDescriptionComponent().toLegacyText());
			//send.setFavicon(serializer.serialize(ping.getFaviconObject()));
			//send.setPingPlayers(serializer.serialize(ping.getPlayers()));
			//ping.getPlayers().getMax(), ping.getModinfo()
			for (Object object : BungeeSockets.sendAll(packet)) {
				if (object != null) {
					ServerPingPacket returned = (ServerPingPacket) object;
					//Because Skript and Bungeecord don't handle these and are kept.
					String input = Skungee.cc(returned.getDescription().replaceAll(Pattern.quote("\\n"), "\n"));
					if (returned.getDescription() != null) ping.setDescriptionComponent(new TextComponent(input));
					if (returned.getVersion() != null) {
						String[] protocol = returned.getVersion().split(Pattern.quote(":"));
						if (protocol.length > 1) ping.setVersion(new Protocol(protocol[0], Integer.parseInt(protocol[1])));
					}
					//if (returned.getPingPlayers() != null) ping.setPlayers((Players) serializer.deserialize(returned.getPingPlayers()));
					//if (returned.getFavicon() != null) ping.setFavicon((Favicon) serializer.deserialize(returned.getFavicon()));
				}
			}
			event.setResponse(ping);
		}
    }
}