package me.limeglass.skungee.bungeecord;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import me.limeglass.skungee.bungeecord.sockets.BungeeSockets;
import me.limeglass.skungee.bungeecord.sockets.ServerTracker;
import me.limeglass.skungee.objects.BungeePacket;
import me.limeglass.skungee.objects.BungeePacketType;
import net.md_5.bungee.api.ProxyServer;

public class VariableStorage {
	
	private final static String folder = Skungee.getInstance().getDataFolder().getAbsolutePath() + File.separator + "variables" + File.separator;
	private static TreeMap<String, Object> variables = new TreeMap<String, Object>(String.CASE_INSENSITIVE_ORDER);
	private final static GsonBuilder gsonBuilder = new GsonBuilder();
	private static final String DELIMITER = ": ";
	private static final String NEW_LINE = "\n";
	private static Boolean loadingHash = false;
	private static FileWriter writer = null;
	private static Gson gson;
	private static File file;
	
	//TODO Make an effect to load all variables from a spigot server to the Bungeecord.
	//TODO Add a read lock to the file.
	
	public static FileWriter getWriter() {
		return writer;
	}
	
	public static Object get(String ID) {
		return variables.get(ID);
	}
	
	public static Integer getSize() {
		return variables.size();
	}
	
	public static void save(Boolean running, Boolean reboot) {
		//running shuts down the stream
		//reboot is for an interval saver
		if (running) {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Date date = new Date();
		new File(folder + File.separator + "backups" + File.separator).mkdir();
		File newFile = new File(folder + File.separator + "backups" + File.separator + date.toString().replaceAll(":", "-") + ".csv");
		try {
			Files.copy(file.toPath(), newFile.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (reboot) {
			load();
		}
	}
	
	public static void setup() {
		if (Skungee.getConfig().getBoolean("NetworkVariables.Backups.Enabled", false)) run();
		new File(folder).mkdir();
		file = new File(folder + "variables.csv");
		gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY);
		gson = gsonBuilder.create();
		if (!file.exists()) {
			try {
				writer = new FileWriter(file);
				writer.append(NEW_LINE);
				writer.append("# Skungee's variable database.");
				writer.append(NEW_LINE);
				writer.append("# Please do not modify this file manually, thank you!");
				writer.append(NEW_LINE);
				writer.append(NEW_LINE);
				Skungee.debugMessage("Successfully created CSV variables database!");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					writer.flush();
				} catch (IOException e) {
					Skungee.debugMessage("Error flushing data during setup!");
					e.printStackTrace();
				}
			}
		} else {
			load();
		}
	}
	
	private static void load() {
		String line = "";
		BufferedReader reader = null;
		try {
			ArrayList<String[]> data = new ArrayList<String[]>();
			reader = new BufferedReader(new FileReader(file));
			for (int i = 0; i < 4; i ++) {
				reader.readLine();
			}
			while ((line = reader.readLine()) != null) {
				String[] values = line.split(DELIMITER, 2);
				data.add(values);
			}
			writer = new FileWriter(file);
			writer.append(NEW_LINE);
			writer.append("# Skungee's variable database.");
			writer.append(NEW_LINE);
			writer.append("# Please do not modify this file manually, thank you!");
			writer.append(NEW_LINE);
			writer.append(NEW_LINE);
			for (String[] varaibleData : data) {
				write(varaibleData[0], gson.fromJson(varaibleData[1], Object.class));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				Skungee.debugMessage("Error closing reader while loading!");
				e.printStackTrace();
			}
		}
	}
	
	private static void loadFromHash() {
		loadingHash = true;
		try {
			writer = new FileWriter(file);
			writer.append(NEW_LINE);
			writer.append("# Skungee's variable database.");
			writer.append(NEW_LINE);
			writer.append("# Please do not modify this file manually, thank you!");
			writer.append(NEW_LINE);
			writer.append(NEW_LINE);
			if (!variables.isEmpty()) {
				for (String ID : variables.keySet()) {
					write(ID, variables.get(ID));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.flush();
			} catch (IOException e) {
				Skungee.debugMessage("Error flushing writer while loading from hash!");
				e.printStackTrace();
			}
		}
		loadingHash = false;
	}
	
	public static void remove(String ID) {
		if (variables.containsKey(ID)) {
			if (!loadingHash) {
				try {
					getWriter().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				variables.remove(ID);
				loadFromHash();
			}
		}
	}
	
	public static void write(String ID, Object object) {
		if (ID == null || object == null) return;
		if (Skungee.getConfig().getBoolean("NetworkVariables.AutomaticSharing", false)) {
			if (!ServerTracker.isEmpty()) {
				BungeePacket packet = new BungeePacket(false, BungeePacketType.UPDATEVARIABLES, ID, object);
				BungeeSockets.sendAll(packet);
			}
		}
		if (variables.containsKey(ID)) {
			if (!loadingHash) {
				if (Skungee.getConfig().getBoolean("NetworkVariables.AllowOverrides", true)) {
					try {
						getWriter().close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					variables.remove(ID);
					loadFromHash();
				} else {
					return;
				}
			}
		}
		variables.put(ID, object);
		try {
			writer.append(ID);
			writer.append(DELIMITER);
			writer.append(gson.toJson(object));
			writer.append(NEW_LINE);
		} catch (IOException e) {
			try {
				writer = new FileWriter(file);
			} catch (IOException e1) {}
		} finally {
			try {
				writer.flush();
			} catch (IOException e) {
				Skungee.debugMessage("Error flushing data while writing!");
				e.printStackTrace();
			}
		}
	}
	
	public static void run() {
		ProxyServer.getInstance().getScheduler().schedule(Skungee.getInstance(), new Runnable() {
			@Override
			public void run() {
				if (Skungee.getConfig().getBoolean("NetworkVariables.Backups.ConsoleMessage", false)) {
					Skungee.consoleMessage("Variables have been saved!");
				}
				save(true, true);
			}
		}, Skungee.getConfig().getLong("NetworkVariables.Backups.IntervalTime", 60), 1, TimeUnit.MINUTES);
	}
}
