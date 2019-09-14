package me.limeglass.skungee.bungeecord;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.jdt.annotation.Nullable;

import me.limeglass.skungee.BungeeConfigSaver;
import me.limeglass.skungee.EncryptionUtil;
import me.limeglass.skungee.UniversalSkungee;
import me.limeglass.skungee.bungeecord.commands.SkungeePasteCommand;
import me.limeglass.skungee.bungeecord.handlercontroller.SkungeeHandler;
import me.limeglass.skungee.bungeecord.handlercontroller.SkungeeHandlerManager;
import me.limeglass.skungee.bungeecord.listeners.EventListener;
import me.limeglass.skungee.bungeecord.managers.PlayerTimeManager;
import me.limeglass.skungee.bungeecord.protocol.channel.ChannelListener;
import me.limeglass.skungee.bungeecord.serverinstances.Premium;
import me.limeglass.skungee.bungeecord.sockets.BungeeRunnable;
import me.limeglass.skungee.bungeecord.sockets.ServerInstancesSockets;
import me.limeglass.skungee.bungeecord.utils.BungeeReflectionUtil;
import me.limeglass.skungee.bungeecord.variables.VariableManager;
import me.limeglass.skungee.spigot.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

/**
 * Bungeecord
 */
public class Skungee extends Plugin {
	private static Map<String, Configuration> files = new HashMap<>();
	private PlayerTimeManager playerTimeManager;
	private static EncryptionUtil encryption;
	private static BungecordMetrics metrics;
	private ServerSocket serverSocket;
	private static Skungee instance;
	private File SCRIPTS_FOLDER;
	private SkungeeBin haste;

	public void onEnable() {
		instance = this;
		if (!getDataFolder().exists())
			getDataFolder().mkdir();
		UniversalSkungee.setBungeecord(true);
		SCRIPTS_FOLDER = new File(getDataFolder(), File.separator + "scripts");
		if (!SCRIPTS_FOLDER.exists())
			SCRIPTS_FOLDER.mkdir();
		loadConfiguration();
		Premium.check();
		playerTimeManager = new PlayerTimeManager(this);
		encryption = new EncryptionUtil(this, false);
		encryption.hashFile();
		//load handlers
		BungeeReflectionUtil.getClasses(this, "me.limeglass.skungee.bungeecord.handlers", "me.limeglass.skungee.bungeecord.protocol.handlers").forEach(clazz -> {
			try {
				if (clazz.equals(SkungeeHandler.class))
					return;
				Object object = clazz.newInstance();
				if (!(object instanceof SkungeeHandler))
					return;
				SkungeeHandler handler = (SkungeeHandler) object;
				SkungeeHandlerManager.registerHandler(handler);
			} catch (InstantiationException | IllegalAccessException e) {}
		});
		this.haste = new SkungeeBin(instance);
		metrics = new BungecordMetrics(this);
		metrics.addCustomChart(new BungecordMetrics.MultiLineChart("variables_and_scripts") {
			@Override
			public HashMap<String, Integer> getValues(HashMap<String, Integer> map) {
				map.put("amount of variables", VariableManager.getMainStorage().getSize());
				map.put("amount of global scripts", SCRIPTS_FOLDER.listFiles().length);
				return map;
			}
		});
		metrics.addCustomChart(new BungecordMetrics.SimplePie("amount_of_plugins") {
			@Override
			public String getValue() {
				return getProxy().getPluginManager().getPlugins().size() + "";
			}
		});
		metrics.addCustomChart(new BungecordMetrics.SimplePie("storage_type") {
			@Override
			public String getValue() {
				return VariableManager.getMainStorage().getNames()[0];
			}
		});
		metrics.addCustomChart(new BungecordMetrics.SimplePie("using_serverinstnaces") {
			@Override
			public String getValue() {
				return Premium.check() + "";
			}
		});
		metrics.addCustomChart(new BungecordMetrics.SimplePie("packets_enabled") {
			@Override
			public String getValue() {
				return Skungee.getConfiguration("config").getBoolean("Packets.Enabled") + "";
			}
		});
		if (getConfig().getBoolean("Events", false))
			getProxy().getPluginManager().registerListener(this, new EventListener());
		if (getConfig().getBoolean("Packets.Enabled", true))
			getProxy().getPluginManager().registerListener(this, new ChannelListener());
		VariableManager.setup();
		connect();
		getProxy().getPluginManager().registerCommand(this, new SkungeePasteCommand());
		if (!getConfig().getBoolean("DisableRegisteredInfo", false))
			consoleMessage("Skungee has been enabled!");
	}

	public void onDisable() {
		ServerInstancesSockets.shutdown();
	}

	private void loadConfiguration() {
		File config = new File(Skungee.getInstance().getDataFolder(), "config.yml");
		try (InputStream in = getResourceAsStream("Bungeecord/config.yml")) {
			if (!config.exists())
				Files.copy(in, config.toPath());
			Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(config);
			if (!getDescription().getVersion().equals(configuration.getString("version"))) {
				new BungeeConfigSaver(instance).execute();
				loadConfiguration();
				//Sends message after configuration has loaded. Important.
				consoleMessage("&eThere is a new Skungee version. Generating new config...");
				return;
			}
			addConfiguration("config", configuration);
		} catch (IOException e) {
			Skungee.exception(e, "Could not create and save serverinstances due to new configuration.");
		}
	}

	private void connect () {
		int port = getConfig().getInt("port", 1337);
		try {
			String address = getConfig().getString("bind-to-address", "localhost");
			if (Utils.matchesIgnoreCase(address, "localhost", "0.0.0.0", "127.0.0.1"))
				serverSocket = new ServerSocket(port);
			else
				serverSocket = new ServerSocket(port, 50, InetAddress.getByName(address.trim()));
			serverSocket.setReceiveBufferSize(getConfig().getInt("buffer-size", 10240));
			consoleMessage("connection established on address " + serverSocket.getInetAddress().getHostAddress() + " on port " + port);
			ProxyServer.getInstance().getScheduler().runAsync(getInstance(), new Runnable() {
				@Override
				public void run() {
					while (!serverSocket.isClosed()) {
						try {
							new Thread(new BungeeRunnable(serverSocket.accept())).start();
						} catch (IOException e) {
							Skungee.exception(e, "Socket couldn't be accepted.");
						}
					}
				}
			});
		} catch (IOException e) {
			Skungee.exception(e, "ServerSocket couldn't be created on port: " + port);
		}
	}

	public String postSkungeeHaste() {
		String content = haste.createHaste();
		return haste.postHaste(content);
	}

	public PlayerTimeManager getPlayerTimeManager() {
		return playerTimeManager;
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
		infoMessage("[Skungee] Severe Error: " + Arrays.toString(info));
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
		infoMessage("Report this at https://github.com/TheLimeGlass/Skungee/issues. You can also message one of the Skungee developers.");
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

	public File getScriptsFolder() {
		return SCRIPTS_FOLDER;
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

	public Map<String, Configuration> getFiles() {
		return files;
	}

	//Grabs a Configuration of a defined name. The name can't contain .yml in it.
	@Deprecated
	public static Configuration getConfiguration(String file) {
		return (files.containsKey(file)) ? files.get(file) : null;
	}

//	/**
//	 * Grabs a Configuration of a defined name. The name can't contain .yml in it.
//	 * 
//	 * @param file The name to check and grab if present.
//	 * @return Optional<Configuration> if found.
//	 */
//	public Optional<Configuration> getConfiguration(String file) {
//		return Optional.ofNullable(files.get(file));
//	}

	public static void addConfiguration(String name, Configuration configuration) {
		files.put(name, configuration);
	}

	public static void debugMessage(String text) {
		if (getConfig().getBoolean("debug")) consoleMessage("&b" + text);
	}

	public static String cc(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}

	public static void infoMessage(@Nullable String... messages) {
		if (messages != null && messages.length > 0) {
			for (String text : messages) ProxyServer.getInstance().getLogger().info("[Skungee] " + text);
		} else {
			ProxyServer.getInstance().getLogger().info("");
		}
	}

	public static void consoleMessage(@Nullable String... messages) {
		if (getConfig().getBoolean("DisableConsoleMessages", false)) return;
		if (messages != null && messages.length > 0) {
			for (String text : messages) {
				if (getConfig().getBoolean("DisableConsoleColour", false)) infoMessage(ChatColor.stripColor(cc(text)));
				else ProxyServer.getInstance().getLogger().info(cc("&8[&cSkungee&8] &e" + text));
			}
		} else {
			ProxyServer.getInstance().getLogger().info("");
		}
	}

}
