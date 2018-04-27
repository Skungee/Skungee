package me.limeglass.skungee.spigot.sockets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.limeglass.skungee.UniversalSkungee;
import me.limeglass.skungee.spigot.Skungee;
import me.limeglass.skungee.objects.BungeePacket;
import me.limeglass.skungee.objects.events.EvtPlayerDisconnect;
import me.limeglass.skungee.objects.events.EvtPlayerSwitchServer;

import java.util.UUID;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.config.Config;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.variables.Variables;

public class SpigotPacketHandler {
	
	//TODO Possible cleanup and place this into an abstract with different packet classes.
	
	public static Object handlePacket(BungeePacket packet, InetAddress address) {
		Skungee.debugMessage("Recieved " + UniversalSkungee.getPacketDebug(packet));
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
					Bukkit.getPluginManager().callEvent(new EvtPlayerDisconnect((String)packet.getObject(), packet.getPlayers()[0]));
				}
				break;
			case PLAYERSWITCH:
				if (packet.getObject() != null && packet.getPlayers() != null) {
					Bukkit.getPluginManager().callEvent(new EvtPlayerSwitchServer((String)packet.getObject(), packet.getPlayers()[0]));
				}
				break;
			case PLAYERLOGIN:
				OfflinePlayer playerLogin = Bukkit.getOfflinePlayer((UUID) packet.getObject());
				if (playerLogin != null) {
				//	Bukkit.getPluginManager().callEvent(new EvtBungeecordConnect((UUID) packet.getObject(), playerLogin));
				}
				break;
			case PLAYERCOMMAND:
				@SuppressWarnings("unchecked") ArrayList<Object> dataCommand = (ArrayList<Object>) packet.getSetObject();
				OfflinePlayer playerCommand = Bukkit.getOfflinePlayer((UUID) dataCommand.get(0));
				if (playerCommand != null) {
					//username, offlineplayer, command
					//Bukkit.getPluginManager().callEvent(new EvtBungeecordCommand((String) packet.getObject(), (UUID) dataCommand.get(0), playerCommand, (String) dataCommand.get(1)));
				}
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
			case GLOBALSCRIPTS: //TODO make this pretty
				@SuppressWarnings("unchecked")
				HashMap<String, List<String>> data = (HashMap<String, List<String>>) packet.getObject();
				FilenameFilter enabledFilter = new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						return name.toLowerCase().endsWith(".sk") && !name.startsWith("-");
					}
				};
				FilenameFilter allFilter = new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						return name.toLowerCase().endsWith(".sk") && !name.startsWith("-");
					}
				};
				try {
					File scriptsFolder = new File(Skript.getInstance().getDataFolder().getAbsolutePath() + File.separator + Skript.SCRIPTSFOLDER);
					if (scriptsFolder.list().length == 0) {
						for (String name : data.keySet()) {
							List<String> lines = data.get(name);
							File tempFile = File.createTempFile("Skellett", name);
							PrintStream out = new PrintStream(new FileOutputStream(tempFile));
							out.print(StringUtils.join(lines, '\n'));
							out.close();
							File newLoc = new File(scriptsFolder + File.separator + name);
							if (!newLoc.exists()) {
								FileUtils.moveFile(tempFile, newLoc);
							}
							if (Skript.methodExists(ScriptLoader.class, "loadStructures", File[].class)) {
								ScriptLoader.loadStructures(new File[] {newLoc});
							}
							List<Config> configs = ScriptLoader.loadStructures(new File[] {newLoc});
							ScriptLoader.loadScripts(configs);
							//if (Skellett.spData.getBoolean("GlobalScriptReloadMessage")) {
							//	Skellett.consoleMessage("&6GlobalScripts: created " + name + " for this server!");
							//}
							tempFile.delete();
						}
					} else {
						File[] scriptsArray = getFiles(scriptsFolder, enabledFilter);
						ArrayList<String> scripts = new ArrayList<String>();
						for (File s : getFiles(scriptsFolder, allFilter)) {
							scripts.add(s.getName());
						}
						scriptsLoop: for (File script : scriptsArray) {
							for (String name : data.keySet()) {
								List<String> lines = data.get(name);
								File tempFile = File.createTempFile("Skellett", name);
								PrintStream out = new PrintStream(new FileOutputStream(tempFile));
								out.print(StringUtils.join(lines, '\n'));
								out.close();
								if (script.getName().equals(name)) {
									if (FileUtils.contentEquals(script, tempFile)) {
										tempFile.delete();
										continue scriptsLoop;
									} else {
										try {
											Method method = ScriptLoader.class.getDeclaredMethod("unloadScript", File.class);
											method.setAccessible(true);
											method.invoke(null, script);
										} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
											e1.printStackTrace();
										}
										FileUtils.deleteQuietly(script);
										FileUtils.moveFile(tempFile, script);
										if (Skript.methodExists(ScriptLoader.class, "loadStructures", File[].class)) {
											ScriptLoader.loadStructures(new File[] {script});
										}
										List<Config> configs = ScriptLoader.loadStructures(new File[] {script});
										ScriptLoader.loadScripts(configs);
										//if (Skellett.spData.getBoolean("GlobalScriptReloadMessage")) {
										//	Skellett.consoleMessage("&6GlobalScript " + name + " has been reloaded!");
										//}
										tempFile.delete();
										continue scriptsLoop;
									}
								} else if (!scripts.contains(name)) {
									File newLoc = new File(scriptsFolder + File.separator + name);
									if (!newLoc.exists()) {
										FileUtils.moveFile(tempFile, newLoc);
									}
									if (Skript.methodExists(ScriptLoader.class, "loadStructures", File[].class)) {
										ScriptLoader.loadStructures(new File[] {newLoc});
									}
									List<Config> configs = ScriptLoader.loadStructures(new File[] {newLoc});
									ScriptLoader.loadScripts(configs);
									//if (Skellett.spData.getBoolean("GlobalScriptReloadMessage")) {
									//	Skellett.consoleMessage("&6GlobalScripts: created " + name + " for this server!");
									//}
									scripts.add(name);
									tempFile.delete();
									continue scriptsLoop;
								}
								tempFile.delete();
							}
						}
					}
				} catch (IOException error) {
					error.printStackTrace();
				}
				break;
			case UPDATEVARIABLES:
				String ID = (String) packet.getObject();
				Variables.setVariable(ID, packet.getSetObject(), null, false);
				break;
		}
		return null;
	}
	
	private static ArrayList<File> getListFiles(File root, final FilenameFilter filter, ArrayList<File> toAdd) {
		for (File f : root.listFiles()) {
			if (f.isDirectory()) toAdd.addAll(getListFiles(f, filter, toAdd));
			else if (filter.accept(f, f.getName())) toAdd.add(f);
		}
		return toAdd;
	}
	
	public static File[] getFiles(File root, FilenameFilter filter) {
		ArrayList<File> files = getListFiles(root, filter, new ArrayList<File>());
		return files.toArray(new File[files.size()]);
	}
}