package me.limeglass.skungee.proxy.handlers;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;

import me.limeglass.skungee.Skungee;
import me.limeglass.skungee.bungeecord.BungeecordConfiguration;
import me.limeglass.skungee.common.objects.SkungeeServer;
import me.limeglass.skungee.common.objects.SkungeeEnums.ChatMode;
import me.limeglass.skungee.common.objects.SkungeeEnums.HandSetting;
import me.limeglass.skungee.common.objects.SkungeeEnums.SkriptChangeMode;
import me.limeglass.skungee.common.packets.ProxyPacket;
import me.limeglass.skungee.common.packets.ProxyPacketType;
import me.limeglass.skungee.common.packets.ServerInstancesPacket;
import me.limeglass.skungee.common.packets.ServerInstancesPacketType;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.player.PacketPlayer;
import me.limeglass.skungee.common.player.SkungeePlayer;
import me.limeglass.skungee.proxy.sockets.ProxySockets;
import me.limeglass.skungee.proxy.sockets.ServerInstancesSockets;
import me.limeglass.skungee.proxy.sockets.ServerTracker;
import me.limeglass.skungee.spigot.utils.Utils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class SkungeePacketHandler {

	@SuppressWarnings("deprecation")
	public static Object handlePacket(ServerPacket packet, InetAddress address) {
		List<ProxiedPlayer> players = new ArrayList<ProxiedPlayer>();
		if (packet.getPlayers() != null) {
			for (SkungeePlayer player : packet.getPlayers()) {
				ProxiedPlayer proxiedPlayer = null;
				if (((BungeecordConfiguration) Skungee.getPlatform().getConfiguration()).acceptIncomingUUID()&& player.getUUID() != null) {
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
		switch (packet.getType()) {
			case SERVERPLAYERS:
				if (packet.getObject() != null) {
					Set<PacketPlayer> skungeePlayers = new HashSet<>();
					for (String server : (String[]) packet.getObject()) {
						if (ProxyServer.getInstance().getServerInfo(server) != null) {
							for (ProxiedPlayer player : ProxyServer.getInstance().getServerInfo(server).getPlayers()) {
								skungeePlayers.add(new PacketPlayer(player.getUniqueId(), player.getName()));
							}
						}
					}
					return skungeePlayers;
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
			case ISSERVERONLINE:
				if (packet.getObject() != null) {
					if (packet.getObject() instanceof String) {
						SkungeeServer[] checkServers = ServerTracker.get((String)packet.getObject());
						return (checkServers != null && ServerTracker.isResponding(checkServers[0]));
					} else {
						List<Boolean> list = new ArrayList<Boolean>();
						String[] array = (String[])packet.getObject();
						for (int i = 0; i < array.length; i++) {
							SkungeeServer[] checkServers = ServerTracker.get(array[i]);
							list.add(checkServers != null && ServerTracker.isResponding(checkServers[0]));
						}
						return (list.isEmpty()) ? null : list;
					}
				}
				return false;
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
			case SHUTDOWNSERVER:
				if (packet.getObject() != null) {
					ServerInstancesPacket unload = new ServerInstancesPacket(false, ServerInstancesPacketType.SHUTDOWN, (String[]) packet.getObject());
					if (packet.getSetObject() != null) unload = new ServerInstancesPacket(false, ServerInstancesPacketType.SHUTDOWN, packet.getObject(), packet.getSetObject());
					ServerInstancesSockets.send(unload);
					ProxyPacket shutdown = new ProxyPacket(false, ProxyPacketType.SHUTDOWN);
					for (String server : (String[]) packet.getObject()) {
						ProxySockets.send(shutdown, ServerTracker.get(server));
					}
				}
				break;
			case SERVERINSTANCES:
				return ServerInstancesSockets.send(new ServerInstancesPacket(true, ServerInstancesPacketType.SERVERINSTANCES));
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
			default:
				break;
		}
		return null;
	}
}