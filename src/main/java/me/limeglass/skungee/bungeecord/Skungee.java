package me.limeglass.skungee.bungeecord;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.Nullable;

import me.limeglass.skungee.EncryptionUtil;
import me.limeglass.skungee.UniversalSkungee;
import me.limeglass.skungee.bungeecord.listeners.EventListener;
import me.limeglass.skungee.bungeecord.servers.ServerManager;
import me.limeglass.skungee.bungeecord.servers.WrappedServer;
import me.limeglass.skungee.bungeecord.sockets.SocketRunnable;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class Skungee extends Plugin {
	
	//Bungeecord
	
	private static Map<String, Configuration> files = new HashMap<String, Configuration>();
	private final static String prefix = "&8[&cSkungee&8] &e";
	private final static String nameplate = "[Skungee] ";
	private static EncryptionUtil encryption;
	private static BungecordMetrics metrics;
	private File SCRIPTS_FOLDER;
	private ServerSocket serverSocket;
	private static Skungee instance;
	
	public void onEnable(){
		instance = this;
		if (!getDataFolder().exists()) getDataFolder().mkdir();
		UniversalSkungee.setBungeecord(true);
		ServerManager.setup();
		SCRIPTS_FOLDER = new File(getDataFolder(), File.separator + "scripts");
		if (!SCRIPTS_FOLDER.exists()) SCRIPTS_FOLDER.mkdir();
		for (String name : Arrays.asList("config", "serverinstances")) {
			try (InputStream in = getResourceAsStream("Bungeecord/" + name + ".yml")) {
				File file = new File(getDataFolder(), name + ".yml");
				if (!file.exists()) Files.copy(in, new File(getDataFolder(), name + ".yml").toPath());
				Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
				files.put(name, configuration);
			} catch (IOException e) {
				exception(e, "Could not create file: " + name + ".yml");
			}
		}
		//TODO Move this to it's own update checker soon.
		File config = new File(getDataFolder(), "config.yml");
		if (!getDescription().getVersion().equals(getConfig().getString("version"))) {
			consoleMessage("&eThere is a new Skungee version. Generating new config...");
			try (InputStream in = getResourceAsStream("Bungeecord/config.yml")) {
				Files.delete(config.toPath());
				Files.copy(in, config.toPath());
				files.put("config", ConfigurationProvider.getProvider(YamlConfiguration.class).load(config));
			} catch (IOException e) {
				exception(e, "Could not create and save config due to new version.");
			}
		}
		File serverinstances = new File(getDataFolder(), "serverinstances.yml");
		if (getConfiguration("serverinstances").getInt("configuration-version", 0) < 1) {
			consoleMessage("&eThere is a new Skungee serverinstances configuration. Generating new serverinstances.yml...");
			try (InputStream in = getResourceAsStream("Bungeecord/serverinstances.yml")) {
				Files.delete(serverinstances.toPath());
				Files.copy(in, serverinstances.toPath());
				files.put("serverinstances", ConfigurationProvider.getProvider(YamlConfiguration.class).load(serverinstances));
			} catch (IOException e) {
				exception(e, "Could not create and save serverinstances due to new configuration.");
			}
		}
		encryption = new EncryptionUtil(this, false);
		encryption.hashFile();
		metrics = new BungecordMetrics(this);
		metrics.addCustomChart(new BungecordMetrics.SimplePie("amount_of_plugins") {
			@Override
			public String getValue() {
				return getProxy().getPluginManager().getPlugins().size() + "";
			}
		});
		metrics.addCustomChart(new BungecordMetrics.SingleLineChart("amount_of_network_variables") {
			@Override
			public int getValue() {
				return VariableStorage.getSize();
			}
		});
		metrics.addCustomChart(new BungecordMetrics.SingleLineChart("amount_of_global_scripts") {
			@Override
			public int getValue() {
				return SCRIPTS_FOLDER.listFiles().length;
			}
		});
		if (getConfig().getBoolean("Events", false)) getProxy().getPluginManager().registerListener(this, new EventListener());
		VariableStorage.setup();
		connect();
		final WrappedServer server = new WrappedServer("Test");
		ProxyServer.getInstance().getScheduler().schedule(instance, new Runnable() {
			@Override
			public void run() {
				server.shutdown();
			}
		}, 1, TimeUnit.MINUTES);
		if (!getConfig().getBoolean("DisableRegisteredInfo", false)) consoleMessage("has been enabled!");
	}
	
	private void connect () {
		try {
			serverSocket = new ServerSocket(getConfig().getInt("port", 1337), 69);
			consoleMessage("connection established on port " + getConfig().getInt("port", 1337));
			ProxyServer.getInstance().getScheduler().runAsync(getInstance(), new Runnable() {
				@Override
				public void run() {
					while (!serverSocket.isClosed()) {
						try {
							new Thread(new SocketRunnable(serverSocket.accept())).start();
						} catch (IOException e) {
							Skungee.exception(e, "Socket couldn't be accepted.");
						}
					}
				}
			});
		} catch (IOException e) {
			Skungee.exception(e, "ServerSocket couldn't be created on port: " + getConfig().getInt("port", 1337));
		}
	}
	
	//TODO Move this to UniversalSkungee soon
	@SuppressWarnings("deprecation")
	public final static void exception(Throwable cause, String... info) {
		Map<String, PluginDescription> plugins = new HashMap<String, PluginDescription>();
		for (Plugin plugin : ProxyServer.getInstance().getPluginManager().getPlugins()) {
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
		infoMessage(getNameplate() + "Severe Error: " + Arrays.toString(info));
		infoMessage();
		infoMessage("Something went wrong within Skungee.");
		infoMessage("Please report this error to the developers of Skungee so we can fix this from happening in the future.");
		Set<PluginDescription> stackPlugins = new HashSet<>();
		for (StackTraceElement stackTrace : Thread.currentThread().getStackTrace()) {
			for (Entry<String, PluginDescription> entry : plugins.entrySet()) {
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
			for (PluginDescription desc : stackPlugins) {
				pluginsMessage.append(desc.getName());
				pluginsMessage.append(" ");
			}
			infoMessage(pluginsMessage.toString());
			infoMessage("You should try disabling those plugins one by one, trying to find which one causes it.");
			infoMessage("If the error doesn't disappear even after disabling all listed plugins, it is probably a Skungee issue.");
		}
		infoMessage();
		infoMessage("Report this on the Skungee discussion page on SkUnity or Spigot. You can also message one of the Skungee developers.");
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
		infoMessage("  Skungee: " + getConfig().getString("version"));
		infoMessage("  Bungee: " + ProxyServer.getInstance().getVersion());
		infoMessage("  Game version: " + ProxyServer.getInstance().getGameVersion());
		infoMessage("  Protocol version: " + ProxyServer.getInstance().getProtocolVersion());
		infoMessage("  Java: " + System.getProperty("java.version") + " (" + System.getProperty("java.vm.name") + " " + System.getProperty("java.vm.version") + ")");
		infoMessage("  OS: " + System.getProperty("os.name") + " " + System.getProperty("os.arch") + " " + System.getProperty("os.version"));
		infoMessage();
		infoMessage("Thread: " + Thread.currentThread());
		infoMessage("Cause: " + Arrays.toString(info));
		infoMessage();
		infoMessage("End of Error.");
		infoMessage();
	}
	
	public static Skungee getInstance() {
		return instance;
	}
	
	public static Configuration getConfig() {
		return getConfiguration("config");
	}
	
	public static BungecordMetrics getMetrics() {
		return metrics;
	}
	
	public static EncryptionUtil getEncrypter() {
		return encryption;
	}
	
	public static String getNameplate() {
		return nameplate;
	}
	
	public static String getPrefix() {
		return prefix;
	}
	
	public File getScriptsFolder() {
		return SCRIPTS_FOLDER;
	}
	
	public static void debugMessage(String text) {
		if (getConfig().getBoolean("debug")) consoleMessage("&b" + text);
	}
	
	//Grabs a Configuration of a defined name. The name can't contain .yml in it.
	public static Configuration getConfiguration(String file) {
		return (files.containsKey(file)) ? files.get(file) : null;
	}

	public static String cc(String string) {
		return ChatColor.translateAlternateColorCodes((char)'&', string);
	}
	
	public static void infoMessage(@Nullable String... messages) {
		if (messages != null && messages.length > 0) {
			for (String text : messages) ProxyServer.getInstance().getLogger().info(getNameplate() + text);
		} else {
			ProxyServer.getInstance().getLogger().info("");
		}
	}

	public static void consoleMessage(@Nullable String... messages) {
		if (getConfig().getBoolean("DisableConsoleMessages", false)) return;
		if (messages != null && messages.length > 0) {
			for (String text : messages) {
				if (getConfig().getBoolean("DisableConsoleColour", false)) infoMessage(ChatColor.stripColor(cc(text)));
				else ProxyServer.getInstance().getLogger().info(cc(prefix + text));
			}
		} else {
			ProxyServer.getInstance().getLogger().info("");
		}
	}
}
/*
TODO:
Added title stuff:

	(Returns SkungeeTitle)
	[new] (skungee|bungee[[ ]cord]) title [with text] %string% [and] [with subtitle %-string%] [[that] lasts] for %timespan%[[,] [with] fade in %-timespan%][[,] [and] [with] fade out %-timespan%]
	
	(show|display|send) %skungeetitle% to bungee[[ ]cord]] [(player|uuid)][s] %strings/players%

Added SkungeeTitle type. This is a custom title object that works on Spigot and Bungeecord. Mainly used internally.

Added string of SkungeeTitle (Returns the main String of the title):

	Has all changers but ADD.
	[(all [[of] the]|the)] (message|string)[s] (of|from) [(skungee|bungee[[ ]cord])] title[s] %skungeetitles%

Added subtitle of SkungeeTitle (Returns the subtitle String):

	Has all changers but ADD.
	[(all [[of] the]|the)] sub[-]title[s] (of|from) [(skungee|bungee[[ ]cord])] title[s] %skungeetitles%
*/