package me.limeglass.skungee.bungeecord;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.jdt.annotation.Nullable;

import me.limeglass.skungee.EncryptionUtil;
import me.limeglass.skungee.UniversalSkungee;
import me.limeglass.skungee.bungeecord.listeners.EventListener;
import me.limeglass.skungee.bungeecord.sockets.SocketRunnable;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class Skungee extends Plugin {
	
	//Bungeecord
	
	private final static String prefix = "&8[&cSkungee&8] &e";
	private final static String nameplate = "[Skungee] ";
	private static EncryptionUtil encryption;
	private static BungecordMetrics metrics;
	private static Configuration config;
	private static File SCRIPTS_FOLDER;
	private ServerSocket serverSocket;
	private static Skungee instance;
	private static File configFile;
	
	public void onEnable(){
		instance = this;
		UniversalSkungee.setBungeecord(true);
		if (!getDataFolder().exists()) getDataFolder().mkdir();
		SCRIPTS_FOLDER = new File(getDataFolder(), File.separator + "scripts");
		if (!SCRIPTS_FOLDER.exists()) SCRIPTS_FOLDER.mkdir();
		getProxy().getPluginManager().registerListener(this, new EventListener());
		Boolean newConfig = false;
		configFile = new File(getDataFolder(), "config.yml");
		if (!configFile.exists()) {
			try (InputStream in = getResourceAsStream("Bungeecord/config.yml")) {
				newConfig = true;
				Files.copy(in, configFile.toPath());
			} catch (IOException e) {
				exception(e, "could not create config.");
			}
		}
		try {
			config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
		} catch (IOException e) {
			exception(e, "could not save config.");
		}
		if (newConfig) consoleMessage("&cNo config was found, generating a new config...");
		if (!getDescription().getVersion().equals(config.getString("version"))) {
			consoleMessage("&eThere is a new Skungee version. Generating new config...");
			try (InputStream in = getResourceAsStream("Bungeecord/config.yml")) {
				Files.delete(configFile.toPath());
				Files.copy(in, configFile.toPath());
			} catch (IOException e) {
				exception(e, "could not create and save config due to new version.");
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
		/*metrics.addCustomChart(new BungecordMetrics.SingleLineChart("amount_of_network_variables") {
			@Override
			public int getValue() {
				return VariableStorage.getSize();
			}
		});*/
		metrics.addCustomChart(new BungecordMetrics.SingleLineChart("amount_of_global_scripts") {
			@Override
			public int getValue() {
				return SCRIPTS_FOLDER.listFiles().length;
			}
		});
		connect();
		VariableStorage.setup();
		if (!config.getBoolean("DisableRegisteredInfo", false)) consoleMessage("has been enabled!");
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
		infoMessage(getNameplate() + "Severe Error:");
		infoMessage(info);
		infoMessage();
		infoMessage("Something went wrong within Skungee.");
		infoMessage("Please report this error to the developers of Skungee so we can fix this from happening in the future.");
		infoMessage();
		Set<PluginDescription> stackPlugins = new HashSet<>();
		for (StackTraceElement stackTrace : Thread.currentThread().getStackTrace()) {
			for (Entry<String, PluginDescription> entry : plugins.entrySet()) {
				if (stackTrace.getClassName().contains(entry.getKey())) {
					stackPlugins.add(entry.getValue());
				}
			}
		}
		if (!stackPlugins.isEmpty()) {
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
		infoMessage("Please report this on the Skungee discussion page on SkUnity or Spigot. You can also message one of the Skungee developers.");
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
		infoMessage();
		infoMessage("End of Error.");
		infoMessage();
	}
	
	public static Skungee getInstance() {
		return instance;
	}
	
	public static Configuration getConfig() {
		return config;
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
	
	public static File getScriptsFolder() {
		return SCRIPTS_FOLDER;
	}
	
	public static void debugMessage(String text) {
		if (config.getBoolean("debug")) consoleMessage("&b" + text);
	}
	
	public static String cc(String string) {
		return net.md_5.bungee.api.ChatColor.translateAlternateColorCodes((char)'&', (String)string);
	}
	
	public static void infoMessage(@Nullable String... messages) {
		if (messages != null && messages.length > 0) {
			for (String text : messages) ProxyServer.getInstance().getLogger().info(getNameplate() + text);
		} else {
			ProxyServer.getInstance().getLogger().info("");
		}
	}

	public static void consoleMessage(@Nullable String... messages) {
		if (config.getBoolean("DisableConsoleMessages", false)) return;
		if (messages != null && messages.length > 0) {
			for (String text : messages) ProxyServer.getInstance().getLogger().info(cc(prefix + text));
		} else {
			ProxyServer.getInstance().getLogger().info("");
		}
	}
}
/*
Added more structure to the version of Bungeecord version expression:
	[the] version of [the] bungee[[ ]cord]
	[the] bungee[[ ]cord[[']s]] version
	
Added Bungeecord name expression, I don't see the usefulness of this but it's here:
 	#This returns the defined name of the bungeecord.
	[the] name of [the] bungee[[ ]cord]
	[the] bungee[[ ]cord[[']s]] name
	
Added disabled commands, this returns all the defined commands that are disabled in the configuration of Bungeecord:
	[(all [[of] the]|the)] bungee[[ ]cord] disabled commands
	bungee[[ ]cord]'s disabled commands
	[(all [[of] the]|the)] disabled commands (on|of|from) [the] bungee[[ ]cord]
	
Added an expression that grabs the list of all plugins on the Bungeecord by name.
	[(all [[of] the]|the)] bungee[[ ]cord][[']s] plugins
	
Added expression to get the throttle of the Bungeecord:
	[the] bungee[[ ]cord[[']s]] throttle [connection] [delay]

Added expression to get the timeout of the Bungeecord:
	[the] bungee[[ ]cord[[']s]] time[ ]out [connection] [delay]

Added expression to get the online state of the Bungeecord:
	[the] bungee[[ ]cord[[']s]] online mode

Re-added all the Redis syntaxes
Note that these syntaxes are untested due to not having setup a Redis network nor having the time to compile or purchase RedisBungee.
If you run into any issues please let us know, but there shouldn't be any issues.
Skungee may only error if RedisBungee is not installed or installed incorrectly.
If the error contains a ClassNotFound or NoMethodFound exception related to RedisBungee, or the servers get halted due to RedisBungee not being found, you will not be receiving help.
There is no check to make sure RedisBungee is installed or not, because it should be known to the user that you need RedisBungee to use RedisBungee syntaxes,
and Skungee will work regardless if RedisBungee is installed or not, just the RedisBungee syntax won't return and probably error.
This hook utilizes version 0.3.8-SNAPSHOT of RedisBungee because that's the latest RedisBungee Maven repository we could find,
and the project seems to rarely get updated.
	Expressions:
		#Returns all the RedisBungee servers
		[(all [[of] the]|the)] redis[( |-)]bungee[[ ]cord] servers
	
		#Returns all the RedisBungee players
		[(all [[of] the]|the)] redis[( |-)]bungee[[ ]cord] players
		
		#Returns all players on the defined RedisBungee proxies
		[(all [[of] the]|the)] redis[( |-)]bungee[[ ]cord] players (on|of|from) [the] prox(ies|y) %strings%
		[(all [[of] the]|the)] players (on|of|from) [the] redis[( |-)]bungee[[ ]cord] prox(ies|y) %strings%
		
		#Returns all the players from a defined RedisBungee server
		[(all [[of] the]|the)] redis[( |-)]bungee[[ ]cord] players (on|of|from) [the] [server[s]] %strings%
		[(all [[of] the]|the)] players (on|of|from) [the] redis[( |-)]bungee[[ ]cord] [server[s]] %strings%
		
		#Returns the RedisBungee ID of the Bungeecord that is connected to this Skungee
		[th(e|is)] [bungee[[ ]cord[[']s]]] redis[( |-)]bungee[[ ]cord] ID
		
		#Returns the RedisBungee names of the defined players.
		[(all [[of] the]|the)] redis[( |-)]bungee[[ ]cord] [user[ ]]name[s] (of|from) [(player|uuid)[s]] %strings/players%
		%strings/players%['s] [(player|uuid)[s]] redis[( |-)]bungee[[ ]cord] [user[ ]]name[s]

		#Returns the last known login time of a RedisBungee player.
		#Returns -1 if the player is unknown or has never joined.
		#This can be a Timespan or a Number, define so in the config.yml of Spigot side Skungee.
		[(all [[of] the]|the)] redis[( |-)]bungee[[ ]cord] last [known] login[s] [time[s]] (of|from) [(player|uuid)[s]] %strings/players%
		%strings/players%['s] [(player|uuid)[s]] redis[( |-)]bungee[[ ]cord] last [known] login[s] [time[s]]
		
		#Returns the proxy ID of the server that the defined RedisBungee player(s) are on.
		[(all [[of] the]|the)] redis[( |-)]bungee[[ ]cord] [player] [proxy] ID (of|from) [(player|uuid)[s]] %strings/players%
		%strings/players%['s] [(player|uuid)[s]] redis[( |-)]bungee[[ ]cord] [player] [proxy] ID
		
		#Returns the server names of the servers that the defined RedisBungee player(s) are on.
		[(all [[of] the]|the)] [(connected|current)] redis[( |-)]bungee[[ ]cord] server[s] (of|from) [(player|uuid)[s]] %strings/players%
		%strings/players%['s] [(player|uuid)[s]] [(connected|current)] redis[( |-)]bungee[[ ]cord] server[s]
		
		#Returns the address(es) of the defined RedisBungee player(s).
		[(all [[of] the]|the)] [(connected|current)] redis[( |-)]bungee[[ ]cord] ip [address[es]] (of|from) [(player|uuid)[s]] %strings/players%
		%strings/players%['s] [(player|uuid)[s]] [(connected|current)] redis[( |-)]bungee[[ ]cord] ip [address[es]]

	Effects:
		#Execute a proxy command on a RedisBungee proxy
		(run|execute) redis[( |-)]bungee[[ ][cord]] [(proxy|console)] command[s] %strings% [(on|of|from) [the] [server[s]] %-strings%]
		make redis[( |-)]bungee[[ ][cord]] (run|execute) [(proxy|console)] command[s] %strings% [(on|of|from) [the] [server[s]] %-strings%]

	Conditions:
		redis[( |-)]bungee[[ ]cord] [(player|uuid)] %string/player% (1¦is|2¦is(n't| not)) online [the] redis[( |-)]bungee[[ ]cord]
		[(player|uuid)] %string/player% (1¦is|2¦is(n't| not)) online [the] redis[( |-)]bungee[[ ]cord]

Added new configuration option for Spigot side Skungee that allows appropriate syntax which return as numbers to be parsed as Timespans.

Potentially fixed the server online condition always returning the same boolean for some.

Fixed a bug within all player expression that was caused when the syntax was used as the player left the Bungeecord network.

Fixed a bug where the SkungeePlayer transformer would make the UUID be default in cases where the name should be higher

Fixed the player execution command effect so that the command actually executes bungeecord commands now
rather than every spigot server. IMPORTANT due to this, you will need to delete your syntax.yml and let it regenerate
with the new syntax so that old syntax don't override or stop this from working. This is only needed if you want this
update to fix this issue. There is a new syntax to handle Commands which takes over the same syntax
so you don't need to modify your scripts if that's the case. Just be sure to have saved the syntax.yml if you have modified syntax

Fixed some issues where the configuration of Skungee would do some weird things, it may still happen for some reason.
This is mainly due to having a configuration open from Skungee, and then updating Skungee and restarting the server.
	
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