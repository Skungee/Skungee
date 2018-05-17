package me.limeglass.skungee.bungeecord.sockets;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import me.limeglass.skungee.UniversalSkungee;
import me.limeglass.skungee.bungeecord.Skungee;
import me.limeglass.skungee.bungeecord.VariableStorage;
import me.limeglass.skungee.objects.BungeePacket;
import me.limeglass.skungee.objects.BungeePacketType;
import me.limeglass.skungee.objects.ChatMode;
import me.limeglass.skungee.objects.ConnectedServer;
import me.limeglass.skungee.objects.HandSetting;
import me.limeglass.skungee.objects.ServerInstancesPacket;
import me.limeglass.skungee.objects.ServerInstancesPacketType;
import me.limeglass.skungee.objects.SkriptChangeMode;
import me.limeglass.skungee.objects.SkungeePacket;
import me.limeglass.skungee.objects.SkungeePacketType;
import me.limeglass.skungee.objects.SkungeePlayer;
import me.limeglass.skungee.objects.SkungeeTitle;
import me.limeglass.skungee.spigot.utils.Utils;

import java.util.concurrent.TimeUnit;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;

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
					for (Entry<String, ServerInfo> server : servers.entrySet()) {
						String serverAddress = server.getValue().getAddress().getAddress().getHostAddress();
						for (Enumeration<NetworkInterface> entry = NetworkInterface.getNetworkInterfaces(); entry.hasMoreElements();) {
							for (Enumeration<InetAddress> addresses = entry.nextElement().getInetAddresses(); addresses.hasMoreElements();) {
								if (addresses.nextElement().getHostAddress().equals(serverAddress) && port == server.getValue().getAddress().getPort()) {
									ConnectedServer connect = new ConnectedServer(usingReciever, recieverPort, port, address, heartbeat, server.getKey(), motd, max, whitelisted);
									if (!ServerTracker.contains(connect)) {
										ServerTracker.add(connect);
										ServerTracker.update(server.getKey());
										return "CONNECTED";
									}
								}
							}
						}
						if (serverAddress.equals(address.getHostAddress()) && port == server.getValue().getAddress().getPort()) {
							ConnectedServer connect = new ConnectedServer(usingReciever, recieverPort, port, address, heartbeat, server.getKey(), motd, max, whitelisted);
							if (!ServerTracker.contains(connect)) {
								ServerTracker.add(connect);
								ServerTracker.update(server.getKey());
								return "CONNECTED";
							}
						}
					}
				} catch (SocketException exception) {
					Skungee.exception(exception, "Could not find the system's local host.");
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
								VariableStorage.remove(ID);
								break;
							case SET:
								VariableStorage.write(ID, value);
								break;
						}
					}
				}
				return null;
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
			case PLAYERPERMISSIONS:
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
			case TITLE:
				if (packet.getObject() == null) return null;
				SkungeeTitle title = (SkungeeTitle) packet.getObject();
				//title.setTitle(ProxyServer.getInstance().createTitle());
				return title;
			case PLAYERTITLE:
				if (packet.getObject() == null || packet.getPlayers() == null) return null;
				//((SkungeeTitle)packet.getObject()).send(packet.getPlayers());
				break;
			case PLAYERCOMMAND:
				Set<Boolean> registered = new HashSet<Boolean>();
				if (!players.isEmpty()) {
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
			case SHUTDOWNSERVER:
				//TODO
				break;
		}
		return null;
	}
}