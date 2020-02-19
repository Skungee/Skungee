package me.limeglass.skungee.proxy.variables;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarFile;

import me.limeglass.skungee.Skungee;
import me.limeglass.skungee.bungeecord.SkungeeBungee;
import me.limeglass.skungee.spigot.utils.ReflectionUtil;
import net.md_5.bungee.api.ProxyServer;

public class VariableManager {
	
	private final static Set<SkungeeStorage> storages = new HashSet<SkungeeStorage>();
	private static SkungeeStorage main;
	
	public static void registerStorage(SkungeeStorage storage) {
		storages.add(storage);
		Skungee.getPlatform().debugMessage("Registered storage type: " + storage.getNames()[0]);
	}
	
	public static void setup() {
		if (SkungeeBungee.getConfig().getBoolean("NetworkVariables.Backups.Enabled", false)) {
			Long time = SkungeeBungee.getConfig().getLong("NetworkVariables.Backups.IntervalTime", 120);
			Boolean messages = SkungeeBungee.getConfig().getBoolean("NetworkVariables.Backups.ConsoleMessage", false);
			ProxyServer.getInstance().getScheduler().schedule(SkungeeBungee.getInstance(), new VariableBackup(messages), time, time, TimeUnit.MINUTES);
		}
		try {
			ReflectionUtil.getClasses(new JarFile(SkungeeBungee.getInstance().getFile()), "me.limeglass.skungee.bungeecord.variables");
		} catch (IOException e) {
			Skungee.getPlatform().exception(e, "Failed to find any classes for Skungee Storage types");
		}
		Boolean initialize = false;
		for (SkungeeStorage storage : storages) {
			for (String name : storage.getNames()) {
				if (SkungeeBungee.getConfig().getString("NetworkVariables.StorageType", "CSV").equalsIgnoreCase(name)) {
					initialize = storage.initialize();
					main = storage;
				}
			}
		}
		if (!initialize) {
			SkungeeBungee.consoleMessage("Failed to initialize storage type: " + SkungeeBungee.getConfig().getString("NetworkVariables.StorageType"));
			return;
		}
	}
	
	public static SkungeeStorage getMainStorage() {
		return main;
	}
	
	public static void backup() {
		main.backup();
	}
}
