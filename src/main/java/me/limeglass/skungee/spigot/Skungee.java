package me.limeglass.skungee.spigot;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import me.limeglass.skungee.EncryptionUtil;
import me.limeglass.skungee.spigot.elements.Register;
import me.limeglass.skungee.spigot.sockets.PacketQueue;
import me.limeglass.skungee.spigot.sockets.Reciever;
import me.limeglass.skungee.spigot.sockets.Sockets;
import me.limeglass.skungee.spigot.utils.ReflectionUtil;
import me.limeglass.skungee.spigot.utils.Utils;

public class Skungee extends JavaPlugin {
	
	//Spigot
	
	private Metrics metrics;
	private SkriptAddon addonInstance;
	private static Skungee instance;
	public FileConfiguration config = getConfig();
	public static File syntaxFile;
	private static FileConfiguration syntaxData;
	private static String packageName = "me.limeglass.skungee.spigot";
	private static String prefix = "&8[&cSkungee&8] &e";
	private static String nameplate = "[Skungee] ";
	private static EncryptionUtil encryption;
	
	public void onEnable(){
		addonInstance = Skript.registerAddon(this).setLanguageFileDirectory("lang");
		instance = this;
		File file = new File(getDataFolder(), "config.yml");
		syntaxFile = new File(getDataFolder(), "Syntax.yml");
		if (!Objects.equals(getDescription().getVersion(), config.getString("version"))) {
			consoleMessage("&dNew update found! Updating files now...");
			if (file.exists()) file.delete();
		}
		for (File f : Arrays.asList(file, syntaxFile)) {
			if (!f.exists()) {
				f.getParentFile().mkdirs();
				saveResource(f.getName(), false);
			}
		}
		syntaxData = new YamlConfiguration();
		try {
			syntaxData.load(syntaxFile);
			addonInstance.loadClasses(getPackageName(), "elements");
		} catch (IOException | InvalidConfigurationException e) {
			exception(e, "Error loading Skript addon classes or loading the syntax file.");
		}
		encryption = new EncryptionUtil(this, true);
		encryption.hashFile();
		if (config.getBoolean("Queue.enabled", true)) {
			PacketQueue.start();
		}
		metrics = new Metrics(this);
		Register.metrics(metrics);
		new Register();
		if (config.getBoolean("Reciever.enabled", false)) {
			Reciever.setupReciever();
		} else {
			Sockets.connect();
		}
		if (!config.getBoolean("DisableRegisteredInfo", false)) Bukkit.getLogger().info(nameplate + "has been enabled!");
	}
	
	public void onDisable() {
		Sockets.onPluginDisabling();
	}

	public final static void exception(Throwable cause, String... info) {
		Map<String, PluginDescriptionFile> plugins = new HashMap<String, PluginDescriptionFile>();
		for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
			if (!plugin.getDescription().getName().equals("Skungee")) {
				String[] parts = plugin.getDescription().getMain().split("\\.");
				StringBuilder name = new StringBuilder(plugin.getDescription().getMain().length());
				for (int i = 0; i < parts.length - 1; i++) {
					name.append(parts[i]).append('.');
				}
				plugins.put(name.toString(), plugin.getDescription());
			}
		}
		infoMessage();
		infoMessage(getNameplate() + "Severe Error:");
		infoMessage(info);
		infoMessage();
		infoMessage("Something went wrong within Skungee.");
		infoMessage("Please report this error to the developers of Skungee so we can fix this from happening in the future.");
		Set<PluginDescriptionFile> stackPlugins = new HashSet<>();
		for (StackTraceElement stackTrace : Thread.currentThread().getStackTrace()) {
			for (Entry<String, PluginDescriptionFile> entry : plugins.entrySet()) {
				if (stackTrace.getClassName().contains(entry.getKey())) {
					stackPlugins.add(entry.getValue());
				}
			}
		}
		if (!stackPlugins.isEmpty()) {
			infoMessage();
			infoMessage("It looks like you are using some plugin(s) that aren't allowing Skungee to work properly.");
			infoMessage("Following plugins are probably related to this error in some way:");
			StringBuilder pluginsMessage = new StringBuilder();
			for (PluginDescriptionFile desc : stackPlugins) {
				pluginsMessage.append(desc.getName());
				pluginsMessage.append(" ");
			}
			infoMessage(pluginsMessage.toString());
			infoMessage("You should try disabling those plugins one by one, trying to find which one causes it.");
			infoMessage("If the error doesn't disappear even after disabling all listed plugins, it is probably a Skungee issue.");
		}
		infoMessage();
		infoMessage("Please report this on the Skungee discussion page on SkUnity or Spigot", "You can also message one of the Skungee developers this error.");
		infoMessage();
		infoMessage("Stack trace:");
		boolean first = true;
		while (cause != null) {
			infoMessage((first ? "" : "Caused by: ") + cause.toString());
			for (final StackTraceElement e : cause.getStackTrace())
				infoMessage("    at " + e.toString());
			cause = cause.getCause();
			first = false;
		}
		infoMessage();
		infoMessage("Information:");
		infoMessage("  Skungee: " + getInstance().getConfig().getString("version"));
		infoMessage("  Bukkit: " + Bukkit.getBukkitVersion());
		infoMessage("  Minecraft: " + ReflectionUtil.getVersion());
		infoMessage("  Java: " + System.getProperty("java.version") + " (" + System.getProperty("java.vm.name") + " " + System.getProperty("java.vm.version") + ")");
		infoMessage("  OS: " + System.getProperty("os.name") + " " + System.getProperty("os.arch") + " " + System.getProperty("os.version"));
		infoMessage();
		infoMessage("Running CraftBukkit: " + Skript.isRunningCraftBukkit());
		infoMessage();
		infoMessage("Thread: " + Thread.currentThread());
		infoMessage();
		infoMessage("End of Error.");
		infoMessage();
	}
	
	public static Skungee getInstance() {
		return instance;
	}
	
	public static EncryptionUtil getEncrypter() {
		return encryption;
	}
	
	public SkriptAddon getAddonInstance() {
		return addonInstance;
	}
	
	public Metrics getMetrics() {
		return metrics;
	}
	
	public static String getPackageName() {
		return packageName;
	}
	
	public static String getNameplate() {
		return nameplate;
	}
	
	public static String getPrefix() {
		return prefix;
	}
	
	public static FileConfiguration getSyntaxData() {
		return syntaxData;
	}
	
	public static void debugMessage(@Nullable String... messages) {
		if (getInstance().getConfig().getBoolean("debug")) {
			for (String text : messages) consoleMessage("&b" + text);
		}
	}
	
	public static void infoMessage(@Nullable String... messages) {
		if (messages != null && messages.length > 0) {
			for (String text : messages) Bukkit.getLogger().info(getNameplate() + text);
		} else {
			Bukkit.getLogger().info("");
		}
	}

	public static void consoleMessage(@Nullable String... messages) {
		if (Skungee.getInstance().getConfig().getBoolean("DisableConsoleMessages", false)) return;
		if (messages != null && messages.length > 0) {
			for (String text : messages) Bukkit.getConsoleSender().sendMessage(Utils.cc(prefix + text));
		} else {
			Bukkit.getLogger().info("");
		}
	}
}
/*



*/