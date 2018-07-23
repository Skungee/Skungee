package me.limeglass.skungee.bungeecord.handlers;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import me.limeglass.skungee.UniversalSkungee;
import me.limeglass.skungee.bungeecord.Skungee;
import me.limeglass.skungee.bungeecord.sockets.BungeeSockets;
import me.limeglass.skungee.bungeecord.sockets.ServerInstancesSockets;
import me.limeglass.skungee.bungeecord.sockets.ServerTracker;
import me.limeglass.skungee.objects.ConnectedServer;
import me.limeglass.skungee.objects.SkungeeEnums.ChatMode;
import me.limeglass.skungee.objects.SkungeeEnums.HandSetting;
import me.limeglass.skungee.objects.SkungeeEnums.SkriptChangeMode;
import me.limeglass.skungee.objects.SkungeePlayer;
import me.limeglass.skungee.objects.packets.BungeePacket;
import me.limeglass.skungee.objects.packets.BungeePacketType;
import me.limeglass.skungee.objects.packets.ServerInstancesPacket;
import me.limeglass.skungee.objects.packets.ServerInstancesPacketType;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import me.limeglass.skungee.spigot.utils.Utils;

import java.util.concurrent.TimeUnit;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

public class PacketHandler {
	
	@SuppressWarnings("deprecation")
	public static Object handlePacket(SkungeePacket packet, InetAddress address) {
		if (!Skungee.getConfig().getBoolean("IgnoreSpamPackets", true)) {
			Skungee.debugMessage("Recieved " + UniversalSkungee.getPacketDebug(packet));
		} else if (!(packet.getType() == SkungeePacketType.HEARTBEAT)) {
			Skungee.debugMessage("Recieved " + UniversalSkungee.getPacketDebug(packet));
		}
		List<ProxiedPlayer> players = new ArrayList<ProxiedPlayer>();
		if (packet.getPlayers() != null) {
			for (SkungeePlayer player : packet.getPlayers()) {
				ProxiedPlayer proxiedPlayer = null;
				if (Skungee.getConfig().getBoolean("IncomingUUIDs", true) && player.getUUID() != null) {
					proxiedPlayer = ProxyServer.getInstance().getPlayer(player.getUUID());
					if (proxiedPlayer == null) { //invalid UUID
						proxiedPlayer = ProxyServer.getInstance().getPlayer(player.getName());
					}
				} else if (player.getName() != null) {
					proxiedPlayer = ProxyServer.getInstance().getPlayer(player.getName());
				}
				if (proxiedPlayer != null) players.add(proxiedPlayer);
			}
		}
		Map<String, ServerInfo> servers = ProxyServer.getInstance().getServers();
		switch (packet.getType()) {
			case HANDSHAKE:
				break;
			case HEARTBEAT:
				break;
			case ACTIONBAR:
				if (!players.isEmpty() && packet.getObject() != null) {
					for (ProxiedPlayer player : players) {
						player.sendMessage(ChatMessageType.ACTION_BAR, new TextComponent((String) packet.getObject()));
					}
				}
				break;
			case PLAYERCHAT:
				if (!players.isEmpty() && packet.getObject() != null) {
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
				if (!players.isEmpty() && packet.getObject() != null) {
					for (ProxiedPlayer player : players) {
						for (String msg : (String[]) packet.getObject()) {
							player.sendMessage(new TextComponent(msg));
						}
					}
				}
				break;
			case CONNECTPLAYER:
				if (!players.isEmpty() && packet.getObject() != null) {
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
						if (packet.getObject() != null && packet.getChangeMode() != null) {
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
				if (packet.getObject() == null || packet.getSetObject() == null) return null;
				String[] evaluations = (String[]) packet.getObject();
				String[] evalServers = (String[]) packet.getSetObject();
				BungeePacket evalPacket = new BungeePacket(false, BungeePacketType.EVALUATE, evaluations);
				for (String server : evalServers) {
					BungeeSockets.send(evalPacket, ServerTracker.get(server));
				}
				break;
			case ISSERVERONLINE:
				if (packet.getObject() != null) {
					if (packet.getObject() instanceof String) {
						ConnectedServer[] checkServers = ServerTracker.get((String)packet.getObject());
						return (checkServers != null && ServerTracker.isResponding(checkServers[0]));
					} else {
						List<Boolean> list = new ArrayList<Boolean>();
						String[] array = (String[])packet.getObject();
						for (int i = 0; i < array.length; i++) {
							ConnectedServer[] checkServers = ServerTracker.get(array[i]);
							list.add(checkServers != null && ServerTracker.isResponding(checkServers[0]));
						}
						return (list.isEmpty()) ? null : list;
					}
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
				break;
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
			case PLAYERVIEWDISTANCE:
				if (!players.isEmpty()) {
					Set<Number> distances = new HashSet<Number>();
					for (ProxiedPlayer player : players) {
						if (player.getViewDistance() > 0) distances.add(player.getViewDistance());
					}
					return distances;
				}
				break;
			case PLAYERRECONNECTSERVER:
				if (!players.isEmpty()) {
					Set<String> reconnected = new HashSet<String>();
					for (ProxiedPlayer player : players) {
						reconnected.add(player.getReconnectServer().getName());
						if (packet.getObject() != null && packet.getChangeMode() != null) {
							if (packet.getChangeMode() == SkriptChangeMode.SET) {
								player.setReconnectServer(ProxyServer.getInstance().getServerInfo((String) packet.getObject()));
							}
						}
					}
					return reconnected;
				}
				break;
			case PLAYERHASPERMISSIONS:
				if (packet.getObject() != null && players != null) {
					if (players.isEmpty()) return false;
					for (String permission : (String[]) packet.getObject()) {
						if (!players.get(0).hasPermission(permission)) {
							return false;
						}
					}
				} else {
					return false;
				}
				return true;
			case PLAYERCOMMAND:
				Set<Boolean> registered = new HashSet<Boolean>();
				if (!players.isEmpty() && packet.getObject() != null) {
					for (ProxiedPlayer player : players) {
						for (String command : (String[]) packet.getObject()) {
							registered.add(ProxyServer.getInstance().getPluginManager().dispatchCommand(player, command));
						}
					}
				}
				return (registered != null && !registered.isEmpty()) ? registered : null;
			case REDISPLAYERS:
				Set<SkungeePlayer> redisPlayers = new HashSet<SkungeePlayer>();
				for (UUID uuid : RedisBungee.getApi().getPlayersOnline()) {
					redisPlayers.add(new SkungeePlayer(false, uuid, ProxyServer.getInstance().getPlayer(uuid).getName()));
				}
				return (redisPlayers != null && !redisPlayers.isEmpty()) ? redisPlayers : null;
			case REDISPROXYPLAYERS:
				if (packet.getObject() == null) return null;
				Set<SkungeePlayer> proxyPlayers = new HashSet<SkungeePlayer>();
				for (String server : (String[]) packet.getObject()) {
					for (UUID uuid : RedisBungee.getApi().getPlayersOnProxy(server)) {
						proxyPlayers.add(new SkungeePlayer(false, uuid, ProxyServer.getInstance().getPlayer(uuid).getName()));
					}
				}
				return (proxyPlayers != null && !proxyPlayers.isEmpty()) ? proxyPlayers : null;
			case REDISSERVERPLAYERS:
				if (packet.getObject() == null) return null;
				Set<SkungeePlayer> serverPlayers = new HashSet<SkungeePlayer>();
				for (String server : (String[]) packet.getObject()) {
					for (UUID uuid : RedisBungee.getApi().getPlayersOnServer(server)) {
						serverPlayers.add(new SkungeePlayer(false, uuid, ProxyServer.getInstance().getPlayer(uuid).getName()));
					}
				}
				return (serverPlayers != null && !serverPlayers.isEmpty()) ? serverPlayers : null;
			case REDISPROXYCOMMAND:
				if (packet.getObject() == null) return null;
				for (String command : (String[]) packet.getObject()) {
					if (packet.getSetObject() != null) {
						for (String server : (String[]) packet.getSetObject()) {
							RedisBungee.getApi().sendProxyCommand(server, command);
						}
					} else {
						RedisBungee.getApi().sendProxyCommand(command);
					}
				}
				break;
			case REDISPLAYERNAME:
				if (packet.getObject() == null || players.isEmpty()) return null;
				Set<SkungeePlayer> names = new HashSet<SkungeePlayer>();
				for (ProxiedPlayer player : players) {
					names.add(new SkungeePlayer(false, player.getUniqueId(), RedisBungee.getApi().getNameFromUuid(player.getUniqueId(), true)));
				}
				return (names != null && !names.isEmpty()) ? names : null;
			case REDISISPLAYERONLINE:
				if (players.isEmpty()) return false;
				return (players != null && RedisBungee.getApi().isPlayerOnline(players.get(0).getUniqueId()));
			case REDISLASTLOGIN:
				if (packet.getObject() == null || players.isEmpty()) return null;
				Set<Number> logins = new HashSet<Number>();
				for (ProxiedPlayer player : players) {
					logins.add(RedisBungee.getApi().getLastOnline(player.getUniqueId()));
				}
				return (logins != null && !logins.isEmpty()) ? logins : null;
			case REDISPLAYERID:
				if (packet.getObject() == null || players.isEmpty()) return null;
				Set<String> IDS = new HashSet<String>();
				for (ProxiedPlayer player : players) {
					IDS.add(RedisBungee.getApi().getProxy(player.getUniqueId()));
				}
				return (IDS != null && !IDS.isEmpty()) ? IDS : null;
			case REDISPLAYERSERVER:
				if (packet.getObject() == null || players.isEmpty()) return null;
				Set<String> redisservers = new HashSet<String>();
				for (ProxiedPlayer player : players) {
					redisservers.add(RedisBungee.getApi().getServerFor(player.getUniqueId()).getName());
				}
				return (redisservers != null && !redisservers.isEmpty()) ? redisservers : null;
			case REDISPLAYERIP:
				if (packet.getObject() == null || players.isEmpty()) return null;
				Set<String> IPS = new HashSet<String>();
				for (ProxiedPlayer player : players) {
					IPS.add(RedisBungee.getApi().getPlayerIp(player.getUniqueId()).getHostName());
				}
				return (IPS != null && !IPS.isEmpty()) ? IPS : null;
			case ISPLAYERONLINE:
				if (players.isEmpty()) return false;
				return (players != null && players.get(0).isConnected());
			case ISUSINGFORGE:
				if (players.isEmpty()) return false;
				return (players != null && players.get(0).isForgeUser());
			case PLAYERCOLOURS:
				if (players.isEmpty()) return false;
				return (players != null && players.get(0).hasChatColors());
			case DISCONNECT:
				if (packet.getObject() != null)	ServerTracker.notResponding(ServerTracker.getByAddress(address, (int)packet.getObject()));
				break;
			case CREATESERVER:
				if (packet.getObject() != null && packet.getSetObject() != null) {
					ServerInstancesSockets.send(new ServerInstancesPacket(false, ServerInstancesPacketType.CREATESERVER, packet.getObject(), packet.getSetObject()));
				}
				break;
			case SKUNGEEMESSAGES:
				if (packet.getObject() == null || packet.getSetObject() == null) return null;
				String[] messages = (String[]) packet.getObject();
				String[] channels = (String[]) packet.getSetObject();
				BungeeSockets.sendAll(new BungeePacket(false, BungeePacketType.SKUNGEEMESSAGES, messages, channels));
				break;
			case TABHEADERFOOTER:
				if (!players.isEmpty() && packet.getObject() != null) {
					BaseComponent component = new TextComponent();
					for (String text : (String[]) packet.getObject()) {
						component.addExtra(text);
					}
					for (ProxiedPlayer player : players) {
						if (player != null) {
							if (packet.getSetObject() instanceof Integer) {
								int pattern = (int) packet.getSetObject();
								if (pattern == 1) player.setTabHeader(component, new TextComponent());
								else if (pattern == 2) player.setTabHeader(new TextComponent(), component);
								else player.setTabHeader(component, component);
							} else {
								BaseComponent secondary = new TextComponent();
								for (String text : (String[]) packet.getSetObject()) {
									secondary.addExtra(text);
								}
								player.setTabHeader(component, secondary);
							}
						}
					}
				}
				break;
			case PLAYERPERMISSIONS:
				if (!players.isEmpty()) {
					if (packet.getObject() != null && packet.getChangeMode() != null) {
						Set<String> permissions = new HashSet<String>();
						for (Object object : (Object[]) packet.getObject()) {
							if (object instanceof String) {
								permissions.add((String)object);
							}
						}
						for (ProxiedPlayer player : players) {
							switch (packet.getChangeMode()) {
								case SET:
									for (String permission : player.getPermissions()) {
										player.setPermission(permission, false);
									}
								case ADD:
									for (String permission : permissions) {
										player.setPermission(permission, true);
									}
									break;
								case RESET:
								case DELETE:
									for (String permission : player.getPermissions()) {
										player.setPermission(permission, false);
									}
									break;
								case REMOVE:
								case REMOVE_ALL:
									for (String permission : permissions) {
										player.setPermission(permission, false);
									}
									break;
							}
						}
						break;
					}
					Set<String> permissions = new HashSet<String>();
					for (ProxiedPlayer player : players) {
						permissions.addAll(player.getPermissions());
					}
					return permissions;
				}
				break;
			case PLAYERGROUPS:
				if (!players.isEmpty()) {
					if (packet.getObject() != null && packet.getChangeMode() != null) {
						String[] groups = new String[((Object[])packet.getObject()).length];
						int i = 0;
						for (Object object : (Object[]) packet.getObject()) {
							if (object instanceof String) {
								groups[i] = (String) object;
							}
						}
						for (ProxiedPlayer player : players) {
							Collection<String> playerGroups = player.getGroups();
							switch (packet.getChangeMode()) {
								case SET:
									player.removeGroups(playerGroups.toArray(new String[playerGroups.size()]));
								case ADD:
									player.addGroups(groups);
									break;
								case RESET:
								case DELETE:
									player.removeGroups(playerGroups.toArray(new String[playerGroups.size()]));
									break;
								case REMOVE:
								case REMOVE_ALL:
									player.removeGroups(groups);
									break;
							}
						}
						break;
					}
					Set<String> groups = new HashSet<String>();
					for (ProxiedPlayer player : players) {
						groups.addAll(player.getGroups());
					}
					return groups;
				}
				break;
			case UNREGISTERCOMMANDS:
				if (packet.getObject() != null) {
					for (String name : (String[])packet.getObject()) {
						PluginManager manager = ProxyServer.getInstance().getPluginManager();
						Plugin plugin = manager.getPlugin(name);
						if (plugin != null && !name.equalsIgnoreCase("skungee")) manager.unregisterCommands(plugin);
					}
				}
				break;
			case UNREGISTERLISTENERS:
				if (packet.getObject() != null) {
					for (String name : (String[])packet.getObject()) {
						PluginManager manager = ProxyServer.getInstance().getPluginManager();
						Plugin plugin = manager.getPlugin(name);
						if (plugin != null && !name.equalsIgnoreCase("skungee")) manager.unregisterListeners(plugin);
					}
				}
				break;
			case SHUTDOWNSERVER:
				if (packet.getObject() != null) {
					ServerInstancesPacket unload = new ServerInstancesPacket(false, ServerInstancesPacketType.SHUTDOWN, (String[]) packet.getObject());
					if (packet.getSetObject() != null) unload = new ServerInstancesPacket(false, ServerInstancesPacketType.SHUTDOWN, packet.getObject(), packet.getSetObject());
					ServerInstancesSockets.send(unload);
					BungeePacket shutdown = new BungeePacket(false, BungeePacketType.SHUTDOWN);
					for (String server : (String[]) packet.getObject()) {
						BungeeSockets.send(shutdown, ServerTracker.get(server));
					}
				}
				break;
			case ENABLEPLUGINS:
				ProxyServer.getInstance().getPluginManager().enablePlugins();
				break;
			case SERVERINSTANCES:
				return ServerInstancesSockets.send(new ServerInstancesPacket(true, ServerInstancesPacketType.SERVERINSTANCES));
			case LOADPLUGINS:
				ProxyServer.getInstance().getPluginManager().loadPlugins();
				break;
			case REDISSERVERS:
				return RedisBungee.getApi().getAllServers();
			case REDISSERVERID:
				return RedisBungee.getApi().getServerId();
			case BUNGEEVERSION:
				return ProxyServer.getInstance().getVersion();
			case CURRENTSERVER:
				return (ServerTracker.getByAddress(address, (int)packet.getObject()) != null) ? ServerTracker.getByAddress(address, (int)packet.getObject()).getName() : null;
			case DISABLEDCOMMANDS:
				return ProxyServer.getInstance().getDisabledCommands();
			case BUNGEENAME:
				return ProxyServer.getInstance().getName();
			case PLUGINS:
				return ProxyServer.getInstance().getPluginManager().getPlugins();
			case BUNGEEPLAYERLIMIT:
				return ProxyServer.getInstance().getConfig().getPlayerLimit();
			case BUNGEETHROTTLE:
				return ProxyServer.getInstance().getConfig().getThrottle();
			case BUNGEETIMEOUT:
				return ProxyServer.getInstance().getConfig().getTimeout();
			case BUNGEEONLINEMODE:
				return ProxyServer.getInstance().getConfig().isOnlineMode();
			case CUSTOM:
				break;
			case BTLP_TABLIST:
				break;
		case BTLP_PLAYERTABLIST:
			break;
		default:
			break;
		}
		return null;
	}
}