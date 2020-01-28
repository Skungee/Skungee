package me.limeglass.skungee.spigot.sockets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.Set;

import me.limeglass.skungee.UniversalSkungee;
import me.limeglass.skungee.spigot.Skungee;
import me.limeglass.skungee.objects.SkungeeVariable.Value;
import me.limeglass.skungee.objects.events.SkungeeMessageEvent;
import me.limeglass.skungee.objects.events.SkungeePingEvent;
import me.limeglass.skungee.objects.events.SkungeePlayerChatEvent;
import me.limeglass.skungee.objects.events.SkungeePlayerCommandEvent;
import me.limeglass.skungee.objects.events.SkungeePlayerDisconnect;
import me.limeglass.skungee.objects.events.SkungeePlayerSwitchServer;
import me.limeglass.skungee.objects.packets.BungeePacket;
import me.limeglass.skungee.objects.packets.BungeePacketType;
import me.limeglass.skungee.objects.packets.ServerPingPacket;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.config.Config;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.Version;
import ch.njol.skript.variables.Variables;

public class SpigotPacketHandler {
	
	//TODO Possible cleanup and place this into an abstract with different packet classes.
	
	public static Object handlePacket(BungeePacket packet, InetAddress address) {
		if (!Skungee.getInstance().getConfig().getBoolean("IgnoreSpamPackets", true)) {
			Skungee.debugMessage("Recieved " + UniversalSkungee.getPacketDebug(packet));
		} else if (!(packet.getType() == BungeePacketType.GLOBALSCRIPTS)) {
			Skungee.debugMessage("Recieved " + UniversalSkungee.getPacketDebug(packet));
		}
		switch (packet.getType()) {
			case PINGSERVER:
				break;
			case PLAYERCOMMAND:
				if (packet.getObject() != null && packet.getSetObject() != null && packet.getPlayers() != null) {
					String server = (String)packet.getSetObject();
					String command = (String)packet.getObject();
					SkungeePlayerCommandEvent event = new SkungeePlayerCommandEvent(command, server, packet.getPlayers());
					Bukkit.getPluginManager().callEvent(event);
					return event.isCancelled();
				}
				break;
			case PLAYERCHAT:
				if (packet.getObject() != null && packet.getSetObject() != null && packet.getPlayers() != null) {
					String server = (String)packet.getSetObject();
					String message = (String)packet.getObject();
					SkungeePlayerChatEvent event = new SkungeePlayerChatEvent(message, server, packet.getPlayers());
					Bukkit.getPluginManager().callEvent(event);
					return event.isCancelled();
				}
				break;
			case PLAYERDISCONNECT:
				if (packet.getObject() != null && packet.getPlayers() != null) {
					Bukkit.getPluginManager().callEvent(new SkungeePlayerDisconnect((String)packet.getObject(), packet.getPlayers()));
				}
				break;
			case PLAYERSWITCH:
				if (packet.getObject() != null && packet.getPlayers() != null) {
					Bukkit.getPluginManager().callEvent(new SkungeePlayerSwitchServer((String)packet.getObject(), packet.getPlayers()));
				}
				break;
			case PLAYERLOGIN:
				//OfflinePlayer playerLogin = Bukkit.getOfflinePlayer((UUID) packet.getObject());
				//if (playerLogin != null) {
				//	Bukkit.getPluginManager().callEvent(new EvtBungeecordConnect((UUID) packet.getObject(), playerLogin));
				//}
				break;
			case EVALUATE:
				if (packet.getObject() != null) {
					for (String effect : (String[]) packet.getObject()) {
						Bukkit.getScheduler().runTask(Skungee.getInstance(), () -> {
							if (Effect.parse(effect, null) == null) {
								Skungee.infoMessage("There was an error executing effect: " + effect);
								Skungee.infoMessage("Possibly not an effect for this server? Make sure you have any addons that could run this effect and that it looks realistic.");
							} else {
								Effect.parse(effect, null).run(null);
							}
						});
					}
				}
				break;
			case GLOBALSCRIPTS:
				if (Skungee.isSkriptPresent()) {
					File scriptsFolder = new File(Skript.getInstance().getDataFolder().getAbsolutePath() + File.separator + Skript.SCRIPTSFOLDER);
					Set<File> scripts = getFiles(scriptsFolder, new FilenameFilter() {
						@Override
						public boolean accept(File dir, String name) {
							return name.toLowerCase().endsWith(".sk") && !name.startsWith("-");
						}
					});
					@SuppressWarnings("unchecked")
					Map<String, List<String>> data = (Map<String, List<String>>) packet.getObject();
					if (Skungee.getInstance().getConfig().getBoolean("GlobalScripts.MimicExact", false)) {
						boolean reload = false;
						for (File script : scripts) {
							if (!data.keySet().parallelStream().anyMatch(name -> name.equals(script.getName()))) {
								reload = true;
								try {
									Files.deleteIfExists(script.toPath());
									if (script.getParentFile().listFiles().length == 0)
										Files.deleteIfExists(script.getParentFile().toPath());
								} catch (IOException e) {}
							}
						}
						if (reload)
							Bukkit.getScheduler().runTask(Skungee.getInstance(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "sk reload scripts"));
					}
					for (Entry<String, List<String>> entry : data.entrySet()) {
						try {
							if (scripts.parallelStream().anyMatch(file -> file.getName().equals(entry.getKey()))) {
								Boolean reload = false;
								File script = File.createTempFile("Skungee", entry.getKey());
								PrintStream out = new PrintStream(new FileOutputStream(script));
								out.print(StringUtils.join(entry.getValue(), '\n'));
								out.close();
								for (File similar : scripts.parallelStream().filter(file -> file.getName().equals(entry.getKey())).collect(Collectors.toSet())) {
									if (!Arrays.equals(Files.readAllBytes(script.toPath()), Files.readAllBytes(similar.toPath()))) {
										Files.deleteIfExists(similar.toPath());
										reload = true;
									}
								}
								if (reload) {
									File newScript = new File(scriptsFolder + File.separator + entry.getKey());
									com.google.common.io.Files.move(script, newScript);
									//String name = scriptsFolder + File.separator + newScript.getName();
									//Config config = new Config(new FileInputStream(newScript), name, newScript, true, false, ":");
									//ScriptLoader.loadScripts(config);
									Bukkit.getScheduler().runTask(Skungee.getInstance(), new Runnable() {
										@Override
										public void run() {
											Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "sk reload " + entry.getKey());
											if (Skungee.getInstance().getConfig().getBoolean("GlobalScripts.Messages", true)) {
												Skungee.consoleMessage("&6GlobalScripts: reloaded script " + entry.getKey() + " for this server!");
											}
										}
									});
								}
								script.delete();
							} else {
								File script = new File(scriptsFolder + File.separator + entry.getKey());
								PrintStream out = new PrintStream(new FileOutputStream(script));
								out.print(StringUtils.join(entry.getValue(), '\n'));
								out.close();
								String name = scriptsFolder + File.separator + script.getName();
								Config config = new Config(new FileInputStream(script), name, true, false, ":");
								if (Skript.getVersion().isLargerThan(new Version("2.2-dev31c"))) {
									config = new Config(new FileInputStream(script), name, script, true, false, ":");
								}
								ScriptLoader.loadScripts(config);
								if (Skungee.getInstance().getConfig().getBoolean("GlobalScripts.Messages", true)) {
									Skungee.consoleMessage("&6GlobalScripts: created script " + entry.getKey() + " for this server!");
								}
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				break;
			case UPDATEVARIABLES:
				if (Skungee.isSkriptPresent()) {
					Object objectName = packet.getObject();
					Object objectValues = packet.getSetObject();
					if (objectName == null || objectValues == null) return null;
					String name = (String) objectName;
					Value[] values = (Value[]) objectValues;
					Object[] objects = new Object[values.length];
					for (int i = 0; i < values.length; i++) {
						objects[i] = Classes.deserialize(values[i].type, values[i].data);
					}
					Variables.setVariable(name, objects, null, false);
				}
				break;
			case SHUTDOWN:
				Bukkit.shutdown();
				break;
			case DISCONNECT:
				Skungee instance = Skungee.getInstance();
				Sockets sockets = instance.getSockets();
				sockets.disconnect();
				Bukkit.getScheduler().cancelTasks(instance);
				Skungee.consoleMessage("The bungeecord was shutdown, ending all tasks...");
				Bukkit.getScheduler().runTaskLater(instance, () -> sockets.keepAlive(), instance.getConfig().getInt("connection.bungeecord-keep-alive-delay", 50));
				break;
			case SERVERLISTPING:
				if (packet instanceof ServerPingPacket) {
					SkungeePingEvent event = new SkungeePingEvent((ServerPingPacket) packet);
					Bukkit.getPluginManager().callEvent(event);
					return event.getPacket();
				}
				break;
			case SKUNGEEMESSAGES:
				if (packet.getObject() != null && packet.getSetObject() != null) {
					for (String channel : (String[])packet.getSetObject()) {
						Bukkit.getScheduler().runTask(Skungee.getInstance(), () -> Bukkit.getPluginManager().callEvent(new SkungeeMessageEvent(channel, (String[])packet.getObject())));
					}
				}
				break;
			default:
				break;
		}
		return null;
	}
	
	private static Set<File> getFiles(File root, final FilenameFilter filter, Set<File> toAdd) {
		for (File file : root.listFiles()) {
			if (file.isDirectory()) toAdd.addAll(getFiles(file, filter, toAdd));
			else if (filter.accept(file, file.getName())) toAdd.add(file);
		}
		return toAdd;
	}
	
	private static Set<File> getFiles(File root, final FilenameFilter filter) {
		Set<File> files = new HashSet<File>();
		for (File file : root.listFiles()) {
			if (file.isDirectory()) files.addAll(getFiles(file, filter, files));
			else if (filter.accept(file, file.getName())) files.add(file);
		}
		return files;
	}

}
