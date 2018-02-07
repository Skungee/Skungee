package me.limeglass.skungee.bungeecord.sockets;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import me.limeglass.skungee.UniversalSkungee;
import me.limeglass.skungee.bungeecord.Skungee;
import me.limeglass.skungee.bungeecord.VariableStorage;
import me.limeglass.skungee.objects.BungeePacket;
import me.limeglass.skungee.objects.BungeePacketType;
import me.limeglass.skungee.objects.ChatMode;
import me.limeglass.skungee.objects.ConnectedServer;
import me.limeglass.skungee.objects.HandSetting;
import me.limeglass.skungee.objects.SkungeePacket;
import me.limeglass.skungee.objects.SkungeePlayer;
import me.limeglass.skungee.spigot.utils.Utils;

import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PacketHandler {
	
	//TODO Possible cleanup and place this into an abstract with different packet classes.
	
	@SuppressWarnings("deprecation")
	public static Object handlePacket(SkungeePacket packet, InetAddress address) {
		if (packet.getType() == null) {
			Skungee.consoleMessage("wat");
		}
		Skungee.debugMessage(UniversalSkungee.getPacketDebug(packet));
		Set<ProxiedPlayer> players = new HashSet<ProxiedPlayer>();
		if (packet.getPlayers() != null) {
			for (SkungeePlayer player : packet.getPlayers()) {
				ProxiedPlayer proxiedPlayer = null;
				if (Skungee.getConfig().getBoolean("IncomingUUIDs", true) && player.getUUID() != null) {
					proxiedPlayer = ProxyServer.getInstance().getPlayer(player.getUUID());
					if (proxiedPlayer == null) { //invalid UUID
						proxiedPlayer = ProxyServer.getInstance().getPlayer(player.getName());
					}
					proxiedPlayer = ProxyServer.getInstance().getPlayer(player.getUUID());
				} else if (player.getName() != null) {
					proxiedPlayer = ProxyServer.getInstance().getPlayer(player.getName());
				}
				if (proxiedPlayer != null && proxiedPlayer.isConnected()) players.add(proxiedPlayer);
			}
		}
		Map<String, ServerInfo> servers = ProxyServer.getInstance().getServers();
		switch (packet.getType()) {
			case PING:
				@SuppressWarnings("unchecked")
				ArrayList<Object> data = (ArrayList<Object>) packet.getObject();
				Boolean usingReciever = (Boolean) data.get(0);
				Integer recieverPort = (Integer) data.get(1);
				Integer port = (Integer) data.get(2);
				@SuppressWarnings("unchecked")
				Set<SkungeePlayer> whitelisted = (Set<SkungeePlayer>) data.get(3);
				Integer heartbeat = (Integer) data.get(4);
				String motd = (String) data.get(5);
				Integer max = (Integer) data.get(6);
				try {
					for (Entry<String, ServerInfo> s : servers.entrySet()) {
						if (s.getValue().getAddress().equals(new InetSocketAddress(address, port)) || Inet4Address.getLocalHost().getHostAddress().equals(new InetSocketAddress(address, port).getAddress().getHostAddress())) {
							ConnectedServer server = new ConnectedServer(usingReciever, recieverPort, port, address, heartbeat, s.getKey(), motd, max, whitelisted);
							if (!ServerTracker.contains(server)) {
								ServerTracker.add(server);
								return "CONNECTED";
							}
						}
					}
				} catch (UnknownHostException e) {
					Skungee.exception(e, "Could not find the systems local host.");
				}
				break;
			case HEARTBEAT:
				Integer fromPort = (Integer) packet.getObject();
				if (fromPort != null) {
					for (Entry<String, ServerInfo> s : servers.entrySet()) {
						try {
							if (s.getValue().getAddress().equals(new InetSocketAddress(address, fromPort)) || Inet4Address.getLocalHost().getHostAddress().equals(new InetSocketAddress(address, fromPort).getAddress().getHostAddress())) {
								return ServerTracker.update(s.getKey());
							}
						} catch (UnknownHostException e) {
							Skungee.exception(e, "Unknown host");
						}
					}
				}
				break;
			case ISPLAYERONLINE:
				return (players != null && players.toArray(new ProxiedPlayer[players.size()])[0].isConnected());
			case ISUSINGFORGE:
				return (players != null && players.toArray(new ProxiedPlayer[players.size()])[0].isForgeUser());
			case ACTIONBAR:
				if (!players.isEmpty()) {
					for (ProxiedPlayer player : players) {
						player.sendMessage(ChatMessageType.ACTION_BAR, new TextComponent((String) packet.getObject()));
					}
				}
				break;
			case PLAYERCHAT:
				if (!players.isEmpty()) {
					for (ProxiedPlayer player : players) {
						for (String msg : (String[]) packet.getObject()) {
							player.chat(ChatColor.stripColor(msg));
						}
					}
				}
				break;
			case KICKPLAYERS:
				String message = "Kicked from the bungeecord network.";
				if (packet.getObject() != null) message = (String) packet.getObject();
				else if (Skungee.getConfig().getBoolean("Misc.UseFunnyKickMessages")) {
					List<String> messages = Skungee.getConfig().getStringList("Misc.FunnyKickMessages");
					Collections.shuffle(messages);
					message = messages.get(0);
				}
				for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
					p.disconnect(new TextComponent(message));
				}
				break;
			case KICKPLAYER:
				if (!players.isEmpty()) {
					String msg = "Kicked from the bungeecord network.";
					if (packet.getObject() != null) msg = (String) packet.getObject();
					else if (Skungee.getConfig().getBoolean("Misc.UseFunnyKickMessages")) {
						List<String> messages = Skungee.getConfig().getStringList("Misc.FunnyKickMessages");
						Collections.shuffle(messages);
						msg = messages.get(0);
					}
					for (ProxiedPlayer player : players) {
						player.disconnect(new TextComponent(msg));
					}
				}
				break;
			case MESSAGEPLAYERS:
				for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
					for (String msg : (String[]) packet.getObject()) {
						p.sendMessage(new TextComponent(msg));
					}
				}
				break;
			case MESSAGEPLAYER:
				if (!players.isEmpty()) {
					for (ProxiedPlayer player : players) {
						for (String msg : (String[]) packet.getObject()) {
							player.sendMessage(new TextComponent(msg));
						}
					}
				}
				break;
			case CONNECTPLAYER:
				if (!players.isEmpty()) {
					for (ProxiedPlayer player : players) {
						ServerInfo serverinfo = ProxyServer.getInstance().getServerInfo((String) packet.getObject());
						if (serverinfo != null) player.connect(serverinfo);
					}
				}
				break;
			case BUNGEECOMMAND:
				if (packet.getObject() != null) {
					if ((long) packet.getSetObject() > (long) 0) {
						int multiplier = 1;
						for (String command : (String[]) packet.getObject()) {
							if (command.startsWith("/")) command = command.substring(1);
							final String intoRunnable = command;
							ProxyServer.getInstance().getScheduler().schedule(Skungee.getInstance(), new Runnable() {
								@Override
								public void run() {
									ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), intoRunnable);
								}
							}, (long) packet.getSetObject() * multiplier, TimeUnit.MILLISECONDS);
							multiplier++;
						}
					} else {
						for (String command : (String[]) packet.getObject()) {
							if (command.startsWith("/")) command = command.substring(1);
							ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), command);
						}
					}
				}
				break;
			case PROXYSTOP:
				if (packet.getObject() != null) {
					String msg = (String) packet.getObject();
					ProxyServer.getInstance().stop(msg);
				} else {
					ProxyServer.getInstance().stop();
				}
				break;
			case BUNGEEVERSION:
				return ProxyServer.getInstance().getVersion();
			case GLOBALPLAYERS:
				Set<SkungeePlayer> allPlayers = new HashSet<SkungeePlayer>();
				for(ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
					allPlayers.add(new SkungeePlayer(false, player.getUniqueId(), player.getName()));
				}
				return allPlayers;
			case SERVERPLAYERS:
				if (packet.getObject() != null) {
					Set<SkungeePlayer> skungeePlayers = new HashSet<SkungeePlayer>();
					for (String server : (String[]) packet.getObject()) {
						if (ProxyServer.getInstance().getServerInfo(server) != null) {
							for (ProxiedPlayer player : ProxyServer.getInstance().getServerInfo(server).getPlayers()) {
								skungeePlayers.add(new SkungeePlayer(false, player.getUniqueId(), player.getName()));
							}
						}
					}
					return skungeePlayers;
				}
				break;
			case ALLSERVERS:
				Set<String> allservers = new HashSet<String>();
				for (Entry<String, ServerInfo> entry : servers.entrySet()) {
					allservers.add(entry.getKey());
				}
				return allservers;
			case PLAYERIP:
				if (!players.isEmpty()) {
					Set<String> addresses = new HashSet<String>();
					for (ProxiedPlayer player : players) {
						addresses.add(player.getAddress().getHostName());
					}
					return addresses;
				}
				break;
			case PLAYERNAME:
				if (!players.isEmpty()) {
					Set<String> names = new HashSet<String>();
					for (ProxiedPlayer player : players) {
						names.add(player.getName());
					}
					return names;
				}
				break;
			case PLAYERSERVER:
				if (!players.isEmpty()) {
					Set<String> playerServers = new HashSet<String>();
					for (ProxiedPlayer player : players) {
						playerServers.add(player.getServer().getInfo().getName());
					}
					return playerServers;
				}
				break;
			case SERVERIP:
				if (packet.getObject() != null) {
					Set<String> addresses = new HashSet<String>();
					for (String server : (String[]) packet.getObject()) {
						ServerInfo serverAddress = ProxyServer.getInstance().getServerInfo(server);
						if (serverAddress != null) {
							addresses.add(serverAddress.getAddress().getHostName());
						}
					}
					return addresses;
				}
				break;
			case SERVERMOTD:
				if (packet.getObject() != null) {
					Set<String> motds = new HashSet<String>();
					for (String server : (String[]) packet.getObject()) {
						ServerInfo serverMotd = ProxyServer.getInstance().getServerInfo(server);
						if (serverMotd != null) {
							motds.add(serverMotd.getMotd());
						}
					}
					return motds;
				}
				break;
			case PLAYERUUID:
				if (!players.isEmpty()) {
					Set<String> uniqueIds = new HashSet<String>();
					for (ProxiedPlayer player : players) {
						uniqueIds.add(player.getUniqueId().toString());
					}
					return uniqueIds;
				}
				break;
			case PLAYERDISPLAYNAME:
				if (!players.isEmpty()) {
					Set<String> names = new HashSet<String>();
					for (ProxiedPlayer player : players) {
						names.add(player.getDisplayName());
						if (packet.getObject() != null && packet.getSetObject() != null) {
							switch (packet.getChangeMode()) {
								case SET:
								case ADD:
									player.setDisplayName((String) packet.getObject());
									break;
								case DELETE:
								case REMOVE:
								case REMOVE_ALL:
								case RESET:
									player.setDisplayName((String) packet.getObject());
									break;
							}
						}
					}
					return names;
				}
				break;
			case BUNGEEPLAYERLIMIT:
				return ProxyServer.getInstance().getConfig().getPlayerLimit();
			case PLAYERPING:
				if (!players.isEmpty()) {
					Set<Number> pings = new HashSet<Number>();
					for (ProxiedPlayer player : players) {
						pings.add(player.getPing());
					}
					return pings;
				}
				break;
			case MAXPLAYERS:
				if (packet.getObject() != null) {
					Set<Number> limits = new HashSet<Number>();
					for (String server : (String[]) packet.getObject()) {
						for (ConnectedServer serverMax : ServerTracker.get(server)) {
							if (serverMax != null && ServerTracker.isResponding(serverMax)) {
								limits.add(serverMax.getMaxPlayers());
							}
						}
					}
					return limits;
				}
				break;
			case EVALUATE:
				String[] evaluations = (String[]) packet.getObject();
				String[] evalServers = (String[]) packet.getSetObject();
				if (evaluations == null || evalServers == null) return null;
				BungeePacket evalPacket = new BungeePacket(false, BungeePacketType.EVALUATE, evaluations);
				for (String server : evalServers) {
					BungeeSockets.send(evalPacket, ServerTracker.get(server));
				}
				break;
			case ISSERVERONLINE:
				if (packet.getObject() != null) {
					Set<Boolean> online = new HashSet<Boolean>();
					for (String server : (String[]) packet.getObject()) {
						for (ConnectedServer onlineServer : ServerTracker.get(server)) {
							online.add(onlineServer != null && ServerTracker.isResponding(onlineServer));
						}
					}
					return online;
				}
				return false;
			case WHITELISTED:
				if (packet.getObject() != null) {
					Set<SkungeePlayer> whitelistedPlayers = new HashSet<SkungeePlayer>();
					for (String server : (String[]) packet.getObject()) {
						for (ConnectedServer serverWhitelisted : ServerTracker.get(server)) {
							if (serverWhitelisted != null && ServerTracker.isResponding(serverWhitelisted)) {
								whitelistedPlayers.addAll(serverWhitelisted.getWhitelistedPlayers());
							}
						}
					}
					return whitelistedPlayers;
				}
				break;
			case NETWORKVARIABLE:
				if (packet.getObject() != null) {
					String ID = (String) packet.getObject();
					if (packet.getChangeMode() == null) {
						return VariableStorage.get(ID);
					} else if (packet.getChangeMode() != null) {
						Object value = (Object) packet.getSetObject();
						switch (packet.getChangeMode()) {
							case ADD:
								packet.setChangeMode(null);
								Object object = handlePacket(packet, address);
								//TODO number shit doesn't work?
								try {
									Number number = (Number) object;
									Skungee.consoleMessage("yes");
									Integer integer = number.intValue();
									VariableStorage.write(ID, integer++);
									break;
								} catch (Exception e) {}
								Set<Object> variable = new HashSet<Object>();
								for (Object obj : (Object[]) object) {
									variable.add(obj);
								}
								variable.add(value);
								VariableStorage.write(ID, variable.toArray(new Object[variable.size()]));
								break;
							case REMOVE:
								packet.setSettableObject(null);
								Object objectRemove = handlePacket(packet, address);
								try {
									Number number = (Number) objectRemove;
									Integer integer = number.intValue();
									VariableStorage.write(ID, integer++);
									break;
								} catch (Exception e) {}
								Set<Object> variableRevove = new HashSet<Object>();
								for (Object obj : (Object[]) objectRemove) {
									variableRevove.add(obj);
								}
								variableRevove.remove(value);
								VariableStorage.write(ID, variableRevove.toArray(new Object[variableRevove.size()]));
								break;
							case DELETE:
							case REMOVE_ALL:
							case RESET:
								Skungee.consoleMessage("test");
								VariableStorage.remove(ID);
								break;
							case SET:
								VariableStorage.write(ID, value);
								break;
						}
					}
				}
				return null;
			case CURRENTSERVER:
				return (ServerTracker.getByAddress(address) != null) ? ServerTracker.getByAddress(address).getName() : null;
			case PLAYERCHATMODE:
				if (!players.isEmpty()) {
					Set<ChatMode> modes = new HashSet<ChatMode>();
					for (ProxiedPlayer player : players) {
						ChatMode chatmode = Utils.getEnum(ChatMode.class, player.getChatMode().toString());
						if (chatmode != null) modes.add(chatmode);
					}
					return modes;
				}
				break;
			case PLAYERHANDSETTING:
				if (!players.isEmpty()) {
					Set<HandSetting> settings = new HashSet<HandSetting>();
					for (ProxiedPlayer player : players) {
						HandSetting chatmode = Utils.getEnum(HandSetting.class, player.getMainHand().toString());
						if (chatmode != null) settings.add(chatmode);
					}
					return settings;
				}
				break;
			}
		
			/*
			case REDISSERVERS:
				if (ProxyServer.getInstance().pluginManager.getPlugin("RedisBungee") != null) {
					return RedisBungee.getApi().getAllServers();
				}
				break;
			case REDISPLAYERS:
				if (ProxyServer.getInstance().pluginManager.getPlugin("RedisBungee") != null) {
					return RedisBungee.getApi().getPlayersOnline();
				}
				break;
			case REDISPLAYERS2:
				if (ProxyServer.getInstance().pluginManager.getPlugin("RedisBungee") != null) {
					return RedisBungee.getApi().getHumanPlayersOnline();
				}
				break;
			case REDISLASTLOGIN:
				if (ProxyServer.getInstance().pluginManager.getPlugin("RedisBungee") != null) {
					return RedisBungee.getApi().getLastOnline(player.getUniqueId());
				}
				break;
			case REDISPROXYPLAYERS:
				if (ProxyServer.getInstance().pluginManager.getPlugin("RedisBungee") != null) {
					return RedisBungee.getApi().getPlayersOnProxy((String) packet.getObject());
				}
				break;
			case REDISSERVERPLAYERS:
				if (ProxyServer.getInstance().pluginManager.getPlugin("RedisBungee") != null) {
					return RedisBungee.getApi().getPlayersOnServer((String) packet.getObject());
				}
				break;
			case REDISPLAYERID:
				if (ProxyServer.getInstance().pluginManager.getPlugin("RedisBungee") != null) {
					return RedisBungee.getApi().getProxy(player.getUniqueId());
				}
				break;
			case REDISPLAYERSERVER:
				if (ProxyServer.getInstance().pluginManager.getPlugin("RedisBungee") != null) {
					return RedisBungee.getApi().getServerFor(player.getUniqueId()).getName();
				}
				break;
			case REDISSERVERID:
				if (ProxyServer.getInstance().pluginManager.getPlugin("RedisBungee") != null) {
					return RedisBungee.getApi().getServerId();
				}
				break;
			case REDISPLAYERNAME:
				if (ProxyServer.getInstance().pluginManager.getPlugin("RedisBungee") != null) {
					return RedisBungee.getApi().getNameFromUuid(player.getUniqueId());
				}
				break;
			case REDISPLAYERNAMELOOKUP:
				if (ProxyServer.getInstance().pluginManager.getPlugin("RedisBungee") != null) {
					return RedisBungee.getApi().getNameFromUuid(player.getUniqueId(), true);
				}
				break;
			case REDISISPLAYERONLINE:
				if (ProxyServer.getInstance().pluginManager.getPlugin("RedisBungee") != null) {
					return RedisBungee.getApi().isPlayerOnline(player.getUniqueId());
				}
				break;
			case REDISPROXYCOMMAND:
				if (ProxyServer.getInstance().pluginManager.getPlugin("RedisBungee") != null) {
					if (packet.getSetObject() != null) {
						RedisBungee.getApi().sendProxyCommand((String) packet.getObject(), (String) packet.getSetObject());
					} else {
						RedisBungee.getApi().sendProxyCommand((String) packet.getObject());
					}
				}
				break;*/
		//default:
		//	break;
		//}
		return null;
	}
}
	