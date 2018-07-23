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
import me.limeglass.skungee.objects.events.PingEvent;
import me.limeglass.skungee.objects.events.PlayerDisconnectEvent;
import me.limeglass.skungee.objects.events.PlayerSwitchServerEvent;
import me.limeglass.skungee.objects.events.SkungeeMessageEvent;
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
			case PLAYERCHAT:
				/*@SuppressWarnings("unchecked")
				ArrayList<Object> data = (ArrayList<Object>) packet.getObject();
				String msg = (String) data.get(1);
				Player reciever = null;
				if (data.get(2) != null) {
					reciever = Bukkit.getPlayer((UUID) data.get(2));
				}
				EvtBungeecordPlayerChat chatEvent = new EvtBungeecordPlayerChat(player, msg, reciever);
				Bukkit.getPluginManager().callEvent(chatEvent);
				if (chatEvent.isCancelled()) {
					return true;
				}*/
				break;
			case PLAYERDISCONNECT:
				if (packet.getObject() != null && packet.getPlayers() != null) {
					Bukkit.getPluginManager().callEvent(new PlayerDisconnectEvent((String)packet.getObject(), packet.getFirstPlayer()));
				}
				break;
			case PLAYERSWITCH:
				if (packet.getObject() != null && packet.getPlayers() != null) {
					Bukkit.getPluginManager().callEvent(new PlayerSwitchServerEvent((String)packet.getObject(), packet.getFirstPlayer()));
				}
				break;
			case PLAYERLOGIN:
				//OfflinePlayer playerLogin = Bukkit.getOfflinePlayer((UUID) packet.getObject());
				//if (playerLogin != null) {
				//	Bukkit.getPluginManager().callEvent(new EvtBungeecordConnect((UUID) packet.getObject(), playerLogin));
				//}
				break;
			case PLAYERCOMMAND:
				//@SuppressWarnings("unchecked") ArrayList<Object> dataCommand = (ArrayList<Object>) packet.getSetObject();
				//OfflinePlayer playerCommand = Bukkit.getOfflinePlayer((UUID) dataCommand.get(0));
				//if (playerCommand != null) {
					//username, offlineplayer, command
					//Bukkit.getPluginManager().callEvent(new EvtBungeecordCommand((String) packet.getObject(), (UUID) dataCommand.get(0), playerCommand, (String) dataCommand.get(1)));
				//}
				break;
			case EVALUATE:
				if (packet.getObject() != null) {
					for (String effect : (String[]) packet.getObject()) {
						if (Effect.parse(effect, null) == null) {
							Skungee.infoMessage("There was an error executing effect: " + effect);
							Skungee.infoMessage("Possibly not an effect for this server? Make sure you have any addons that could run this effect and that it looks realistic.");
						} else {
							Effect.parse(effect, null).run(null);
						}
					}
				}
				break;
			case GLOBALSCRIPTS:
				File scriptsFolder = new File(Skript.getInstance().getDataFolder().getAbsolutePath() + File.separator + Skript.SCRIPTSFOLDER);
				Set<File> scripts = getFiles(scriptsFolder, new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						return name.toLowerCase().endsWith(".sk") && !name.startsWith("-");
					}
				});
				@SuppressWarnings("unchecked")
				Map<String, List<String>> data = (Map<String, List<String>>) packet.getObject();
				for (Entry<String, List<String>> entry : data.entrySet()) {
					try {
						if (scripts.stream().anyMatch(file -> file.getName().equals(entry.getKey()))) {
							Boolean reload = false;
							File script = File.createTempFile("Skungee", entry.getKey());
							PrintStream out = new PrintStream(new FileOutputStream(script));
							out.print(StringUtils.join(entry.getValue(), '\n'));
							out.close();
							for (File similar : scripts.stream().filter(file -> file.getName().equals(entry.getKey())).collect(Collectors.toSet())) {
								if (!Arrays.equals(Files.readAllBytes(script.toPath()), Files.readAllBytes(similar.toPath()))) {
									/*try {
										Method method = ScriptLoader.class.getDeclaredMethod("unloadScript", File.class);
										method.setAccessible(true);
										method.invoke(ScriptLoader.class, similar);
									} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException error) {
										Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "sk reload " + entry.getKey());
									}*/
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
								Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "sk reload " + entry.getKey());
								if (Skungee.getInstance().getConfig().getBoolean("GlobalScriptMessages", true)) {
									Skungee.consoleMessage("&6GlobalScripts: reloaded script " + entry.getKey() + " for this server!");
								}
							}
							script.delete();
						} else {
							File script = new File(scriptsFolder + File.separator + entry.getKey());
							PrintStream out = new PrintStream(new FileOutputStream(script));
							out.print(StringUtils.join(entry.getValue(), '\n'));
							out.close();
							//Config config = ScriptLoader.loadStructure(script);
							String name = scriptsFolder + File.separator + script.getName();
							Config config = new Config(new FileInputStream(script), name, script, true, false, ":");
							ScriptLoader.loadScripts(config);
							if (Skungee.getInstance().getConfig().getBoolean("GlobalScriptMessages", true)) {
								Skungee.consoleMessage("&6GlobalScripts: created script " + entry.getKey() + " for this server!");
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				break;
			case UPDATEVARIABLES:
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
				break;
			case SHUTDOWN:
				Bukkit.shutdown();
				break;
			case SERVERLISTPING:
				if (packet instanceof ServerPingPacket) {
					PingEvent event = new PingEvent((ServerPingPacket) packet);
					Bukkit.getPluginManager().callEvent(event);
					return event.getPacket();
				}
				break;
			case SKUNGEEMESSAGES:
				if (packet.getObject() != null && packet.getSetObject() != null) {
					for (String channel : (String[])packet.getSetObject()) {
						Bukkit.getPluginManager().callEvent(new SkungeeMessageEvent(channel, (String[])packet.getObject()));
					}
				}
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