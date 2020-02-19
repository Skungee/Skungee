package me.limeglass.skungee.bungeecord.events;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import me.limeglass.skungee.bungeecord.SkungeeBungee;
import me.limeglass.skungee.common.objects.ProxyPacketResponse;
import me.limeglass.skungee.common.objects.SkungeeServer;
import me.limeglass.skungee.common.packets.ProxyPacket;
import me.limeglass.skungee.common.packets.ProxyPacketType;
import me.limeglass.skungee.common.packets.ServerPingPacket;
import me.limeglass.skungee.common.player.SkungeePlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.ServerPing.PlayerInfo;
import net.md_5.bungee.api.ServerPing.Protocol;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.protocol.ProtocolConstants;

public class EventListener implements Listener {

	private final SkungeeBungee instance;

	public EventListener(SkungeeBungee instance) {
		this.instance = instance;
	}

	@EventHandler
    public void onServerSwitch(ServerSwitchEvent event) {
		if (event.getPlayer() != null) {
			SkungeePlayer player = new SkungeePlayer(false, event.getPlayer().getUniqueId(), event.getPlayer().getName());
			if (event.getPlayer().getServer() != null) {
				ProxyPacket packet = new ProxyPacket(false, ProxyPacketType.PLAYERSWITCH, event.getPlayer().getServer().getInfo().getName(), null, player);
				instance.sendToAll(packet);
			}
		}
    }

	@EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
		if (event.getPlayer() != null) {
			SkungeePlayer player = new SkungeePlayer(false, event.getPlayer().getUniqueId(), event.getPlayer().getName());
			if (event.getPlayer().getServer() != null) {
				SkungeeServer[] servers = instance.getServerTracker().get(event.getPlayer().getServer().getInfo().getName());
				if (servers == null) return; //Not a valid Skungee connected server
				ProxyPacket packet = new ProxyPacket(false, ProxyPacketType.PLAYERDISCONNECT, servers[0].getName(), null, player);
				instance.sendToAll(packet);
			}
		}
    }

	@SuppressWarnings("deprecation")
	@EventHandler
    public void onCommand(ChatEvent event) {
		Optional<ProxiedPlayer> proxied = ProxyServer.getInstance().getPlayers().parallelStream()
				.filter(p -> p.getAddress().equals(event.getSender().getAddress()))
				.findAny();
		Optional<ServerInfo> serverInfo = ProxyServer.getInstance().getServers().values().parallelStream()
				.filter(s -> s.getAddress().equals(event.getReceiver().getAddress()))
				.findAny();
		if (proxied.isPresent() && serverInfo.isPresent()) {
			ProxiedPlayer p = proxied.get();
			ServerInfo server = serverInfo.get();
			SkungeePlayer player = new SkungeePlayer(false, p.getUniqueId(), p.getName());
			ProxyPacket packet = new ProxyPacket(true, ProxyPacketType.PLAYERCHAT, event.getMessage(), server.getName(), player);
			if (event.isCommand())
				packet = new ProxyPacket(true, ProxyPacketType.PLAYERCOMMAND, event.getMessage(), server.getName(), player);
			if (instance.sendToAll(packet).stream().anyMatch(response -> response.getObject().equals(true)))
				event.setCancelled(true);
		}
    }

	@EventHandler
    public void onPing(ProxyPingEvent event) {
		if (event.getResponse() != null && !instance.getConfiguration().isPingEventDisabled()) {
			ServerPing ping = event.getResponse();
			//This will return all the modified ping event-values from all the scripts on every server.
			//If a script wants the motd to be something but another server wants differently, this will default to the last server as that's not possible. The Bungeecord only has one motd.
			//Only one script should handle modifying the Bungeecord motd. If it's a global script and all the values are the same, Skungee will handle that.
			ServerPingPacket packet = new ServerPingPacket(true, ProxyPacketType.SERVERLISTPING, ProtocolConstants.SUPPORTED_VERSION_IDS);
			packet.setVersion(ping.getVersion().getName() + ":" + ping.getVersion().getProtocol());
			packet.setDescription(ping.getDescriptionComponent().toLegacyText());
			if (ping.getPlayers().getSample() != null) {
				PlayerInfo[] playerInfo = ping.getPlayers().getSample();
				SkungeePlayer[] players = new SkungeePlayer[playerInfo.length];
				int i = 0;
				for (PlayerInfo info : playerInfo) {
					players[i] = new SkungeePlayer(false, info.getUniqueId(), info.getName());
					i++;
				}
				packet.setPlayers(players);
			}
			//send.setPingPlayers(serializer.serialize(ping.getPlayers()));
			//ping.getPlayers().getMax(), ping.getModinfo()
			for (ProxyPacketResponse response : instance.sendToAll(packet)) {
				if (response.getObject() != null) {
					ServerPingPacket returned = (ServerPingPacket) response.getObject();
					//Because Skript and Bungeecord don't handle new line and is kept.
					String input = ChatColor.translateAlternateColorCodes('&', returned.getDescription().replaceAll(Pattern.quote("\\n"), "\n"));
					if (returned.getDescription() != null) ping.setDescriptionComponent(new TextComponent(input));
					if (returned.getVersion() != null) {
						String[] protocol = returned.getVersion().split(Pattern.quote(":"));
						if (protocol.length > 1) ping.setVersion(new Protocol(protocol[0], Integer.parseInt(protocol[1])));
					}
					if (returned.getFavicon() != null) {
						String location = returned.getFavicon();
						try {
							File file = new File(location);
							BufferedImage image;
							if (file.exists() && (location.endsWith(".png") || location.endsWith(".jpg"))) {
								image = ImageIO.read(file);
								if (image.getWidth() != 64 || image.getHeight() != 64 ) {
									instance.consoleMessage("The image at location " + location + " must be exactly 64x64 pixels.");
									image = null;
								}
							} else {
								image = ImageIO.read(new URL(location));
							}
							if (image != null) ping.setFavicon(Favicon.create(image));
						} catch (IOException e) {
							instance.consoleMessage("Could not find URL/Image under " + location + " or the website did not allow/return properly. You can use https://imgur.com/ which is a valid image hosting website.");
						}
					}
					if (returned.getPlayers() != null) {
						PlayerInfo[] info = new PlayerInfo[returned.getPlayers().length];
						int spot = 0;
						for (SkungeePlayer player : returned.getPlayers()) {
							info[spot] = new PlayerInfo(player.getName(), player.getUUID()); 
							spot++;
						}
						ping.getPlayers().setSample(info);
					}
				}
			}
			event.setResponse(ping);
		}
    }

}
