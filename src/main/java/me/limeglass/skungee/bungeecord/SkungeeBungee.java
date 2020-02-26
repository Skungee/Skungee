package me.limeglass.skungee.bungeecord;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.Nullable;

import me.limeglass.skungee.EncryptionUtil;
import me.limeglass.skungee.Skungee;
import me.limeglass.skungee.bungeecord.commands.SkungeePasteCommand;
import me.limeglass.skungee.bungeecord.events.EventListener;
import me.limeglass.skungee.bungeecord.managers.PlayerTimeManager;
import me.limeglass.skungee.common.handlercontroller.SkungeeHandler;
import me.limeglass.skungee.common.handlercontroller.SkungeeHandlerManager;
import me.limeglass.skungee.common.objects.ProxyPacketResponse;
import me.limeglass.skungee.common.objects.SkungeeServer;
import me.limeglass.skungee.common.packets.ProxyPacket;
import me.limeglass.skungee.common.packets.ProxyPacketType;
import me.limeglass.skungee.common.player.PacketPlayer;
import me.limeglass.skungee.common.player.ProxyPlayer;
import me.limeglass.skungee.common.wrappers.ProxyConfiguration;
import me.limeglass.skungee.common.wrappers.ProxyPlatform;
import me.limeglass.skungee.proxy.protocol.channel.ChannelListener;
import me.limeglass.skungee.proxy.sockets.ProxyRunnable;
import me.limeglass.skungee.proxy.sockets.ProxySockets;
import me.limeglass.skungee.proxy.sockets.ServerInstancesSockets;
import me.limeglass.skungee.proxy.sockets.ServerTracker;
import me.limeglass.skungee.proxy.utils.ProxyReflectionUtil;
import me.limeglass.skungee.proxy.variables.VariableManager;
import me.limeglass.skungee.spigot.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerConnectRequest;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent.Reason;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.config.Configuration;

/**
 * Bungeecord
 */
public class SkungeeBungee extends Plugin implements ProxyPlatform {

	private static Map<String, Configuration> files = new HashMap<>();
	private BungeecordConfiguration configuration;
	private PlayerTimeManager playerTimeManager;
	private static SkungeeBungee instance;
	private EncryptionUtil encryption;
	private ServerSocket serverSocket;
	private BungecordMetrics metrics;
	private ServerTracker tracker;
	private ProxySockets sockets;
	private File SCRIPTS_FOLDER;
	private ScheduledTask task;
	private SkungeeBin haste;

	public void onEnable() {
		instance = this;
		if (!getDataFolder().exists())
			getDataFolder().mkdir();
		configuration = new BungeecordConfiguration(this);
		SCRIPTS_FOLDER = new File(getDataFolder(), File.separator + "scripts");
		if (!SCRIPTS_FOLDER.exists())
			SCRIPTS_FOLDER.mkdir();
		tracker = new ServerTracker(this);
		try {
			Skungee.setPlatform(this);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return;
		}
		playerTimeManager = new PlayerTimeManager(this);
		encryption = new EncryptionUtil(this);
		//load handlers
		ProxyReflectionUtil.getClasses(this, "me.limeglass.skungee.proxy.handlers", "me.limeglass.skungee.proxy.protocol.handlers").forEach(clazz -> {
			try {
				if (clazz.equals(SkungeeHandler.class))
					return;
				Object object = clazz.newInstance();
				if (!(object instanceof SkungeeHandler))
					return;
				SkungeeHandler<?> handler = (SkungeeHandler<?>) object;
				SkungeeHandlerManager.registerHandler(handler);
			} catch (InstantiationException | IllegalAccessException e) {}
		});
		haste = new SkungeeBin(instance);
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
		metrics.addCustomChart(new BungecordMetrics.SimplePie("packets_enabled") {
			@Override
			public String getValue() {
				return configuration.allowsPackets() + "";
			}
		});
		if (configuration.allowsEvents())
			getProxy().getPluginManager().registerListener(this, new EventListener(this));
		if (configuration.allowsPackets())
			getProxy().getPluginManager().registerListener(this, new ChannelListener());
		VariableManager.setup();
		connect();
		getProxy().getPluginManager().registerCommand(this, new SkungeePasteCommand());
		consoleMessage("Skungee has been enabled!");
	}

	public void onDisable() {
		task.cancel();
		ServerInstancesSockets.shutdown();
		sockets.sendToAll(new ProxyPacket(false, ProxyPacketType.DISCONNECT));
	}

	private void connect () {
		int port = configuration.getPort();
		try {
			String address = configuration.getBindAddress();
			if (Utils.matchesIgnoreCase(address, "localhost", "0.0.0.0", "127.0.0.1"))
				serverSocket = new ServerSocket(port);
			else
				serverSocket = new ServerSocket(port, 50, InetAddress.getByName(address.trim()));
			serverSocket.setReceiveBufferSize(configuration.getBufferSize());
			consoleMessage("Connection established on address " + serverSocket.getInetAddress().getHostAddress() + " with port " + port);
			task = ProxyServer.getInstance().getScheduler().runAsync(this, () -> {
				while (!serverSocket.isClosed()) {
					try {
						new Thread(new ProxyRunnable(serverSocket.accept(), this)).start();
					} catch (IOException e) {
						exception(e, "Socket couldn't be accepted.");
					}
				}
			});
		} catch (IOException e) {
			exception(e, "ServerSocket couldn't be created on port: " + port);
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
	@Override
	public void exception(Throwable cause, String... info) {
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
		infoMessage("  Skungee: " + configuration.getConfigurationVersion());
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

	@Deprecated
	public static SkungeeBungee getInstance() {
		return instance;
	}

	public BungecordMetrics getMetrics() {
		return metrics;
	}

	public EncryptionUtil getEncrypter() {
		return encryption;
	}

	public Map<String, Configuration> getFiles() {
		return files;
	}

	@Deprecated
	public static void addConfiguration(String name, Configuration configuration) {
		files.put(name, configuration);
	}

	public void debugMessage(String text) {
		if (configuration.debug())
			consoleMessage("&b" + text);
	}

	private String cc(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}

	public void infoMessage(@Nullable String... messages) {
		if (messages != null && messages.length > 0) {
			for (String text : messages) ProxyServer.getInstance().getLogger().info("[Skungee] " + text);
		} else {
			ProxyServer.getInstance().getLogger().info("");
		}
	}

	@Override
	public void consoleMessage(@Nullable String... messages) {
		if (messages != null && messages.length > 0) {
			for (String text : messages) {
				if (configuration.allowsConsoleColour())
					infoMessage(ChatColor.stripColor(cc(text)));
				else
					ProxyServer.getInstance().getLogger().info(cc("&8[&cSkungee&8] &e" + text));
			}
		} else {
			ProxyServer.getInstance().getLogger().info("");
		}
	}

	@Override
	public Platform getPlatform() {
		return Platform.BUNGEECORD;
	}

	@Override
	public ProxyConfiguration getConfiguration() {
		return configuration;
	}

	@Override
	public void schedule(Runnable runnable, long time, TimeUnit unit) {
		ProxyServer.getInstance().getScheduler().schedule(this, runnable, time, unit);
	}

	@Override
	public ServerTracker getServerTracker() {
		return tracker;
	}

	@Override
	public EncryptionUtil getEncryptionUtil() {
		return encryption;
	}

	@Override
	public List<ProxyPacketResponse> sendTo(ProxyPacket packet, SkungeeServer... servers) {
		return sockets.sendTo(packet, servers);
	}

	@Override
	public ProxyPacketResponse send(ProxyPacket packet, SkungeeServer server) {
		return sockets.send(packet, server);
	}

	@Override
	public List<ProxyPacketResponse> sendToAll(ProxyPacket... packets) {
		return sockets.sendToAll(packets);
	}

	@Override
	public void shutdown() {
		ProxyServer.getInstance().stop();
	}

	@Override
	public ProxyPlayer getPlayer(PacketPlayer player) {
		if (!getConfiguration().shouldAcceptIncomingUUID())
			return new BungeePlayer(null, player.getUsername());
		return new BungeePlayer(player);
	}

	@Override
	public void connect(SkungeeServer server, ProxyPlayer... players) {
		ServerInfo info = ProxyServer.getInstance().getServerInfo(server.getName());
		if (info == null)
			return;
		ServerConnectRequest connection = ServerConnectRequest.builder()
				.reason(Reason.PLUGIN)
				.target(info)
				.retry(true)
				.build();
		for (ProxyPlayer player : players)
			((BungeePlayer)player).getPlayer().ifPresent(proxied -> proxied.connect(connection));
	}

	@Override
	public Set<ProxyPlayer> getPlayers() {
		return getProxy().getPlayers().stream()
				.map(player -> new BungeePlayer(player.getUniqueId(), player.getName()))
				.collect(Collectors.toSet());
	}

	@Override
	public ProxyPlayer getPlayer(UUID uuid) {
		ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
		if (player == null)
			return new BungeePlayer(uuid, null);
		return new BungeePlayer(uuid, player.getName());
	}

	@SuppressWarnings("deprecation")
	@Override
	public String getPlatformVersion() {
		return ProxyServer.getInstance().getGameVersion();
	}

}
