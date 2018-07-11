package me.limeglass.skungee.bungeecord.variables;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import me.limeglass.skungee.bungeecord.Skungee;
import me.limeglass.skungee.bungeecord.sockets.BungeeSockets;
import me.limeglass.skungee.bungeecord.sockets.ServerTracker;
import me.limeglass.skungee.objects.BungeePacket;
import me.limeglass.skungee.objects.BungeePacketType;
import me.limeglass.skungee.objects.SkungeeVariable.Value;

public class FlatFileStorage extends SkungeeStorage {

	static {
		registerStorage(new FlatFileStorage());
	}
	
	public FlatFileStorage() {
		super("CSV", "flatfile");
	}
	
	private final static String path = Skungee.getInstance().getDataFolder().getAbsolutePath() + File.separator + "variables" + File.separator;
	private static final String DELIMITER = ": ";
	private Boolean loadingHash = false;
	private File folder, file;
	private FileWriter writer;
	private static Gson gson;
	
	private void header() throws IOException {
		writer.append("\n");
		writer.append("# Skungee's variable database.");
		writer.append("\n");
		writer.append("# Please do not modify this file manually, thank you!");
		writer.append("\n");
		writer.append("\n");
	}
	
	@Override
	public boolean initialize() {
		gson = new GsonBuilder().setLenient().setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create();
		file = new File(path + "variables.csv");
		folder = new File(path);
		folder.mkdir();
		if (!file.exists()) {
			try {
				writer = new FileWriter(file);
				header();
				writer.flush();
				Skungee.debugMessage("Successfully created CSV variables database!");
			} catch (IOException e) {
				Skungee.exception(e, "Failed to create a CSV variable database.");
				return false;
			}
		} else load();
		return true;
	}
	
	@Override
	public Set<Value> get(String index) {
		return variables.get(index);
	}

	@Override
	public void remove(String index) {
		if (variables.containsKey(index)) {
			if (!loadingHash) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				variables.remove(index);
				loadFromHash();
			}
		}
	}

	@Override
	public void backup() {
		//shut down the stream
		try {
			writer.close();
		} catch (IOException e) {
			Skungee.exception(e, "Error closing the variable flatfile writter");
		}
		Date date = new Date();
		new File(folder + File.separator + "backups" + File.separator).mkdir();
		File newFile = new File(folder + File.separator + "backups" + File.separator + date.toString().replaceAll(":", "-") + ".csv");
		try {
			Files.copy(file.toPath(), newFile.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		load();
	}
	
	@SuppressWarnings("unchecked")
	private void load() {
		String line = "";
		BufferedReader reader = null;
		try {
			//Key = index, Value = string serialized value.
			Map<String, String> map = new HashMap<String, String>();
			reader = new BufferedReader(new FileReader(file));
			//Skip the information at the top of the variables.csv file.
			for (int i = 0; i < 4; i ++) {
				reader.readLine();
			}
			while ((line = reader.readLine()) != null) {
				String[] values = line.split(DELIMITER, 2);
				map.put(values[0], values[1]);
			}
			writer = new FileWriter(file);
			header();
			for (Entry<String, String> data : map.entrySet()) {
				set(data.getKey(), gson.fromJson(data.getValue(), Set.class));
			}
			reader.close();
		} catch (IOException e) {
			Skungee.exception(e, "Failed to load and write variables.");
		}
	}
	
	@Override
	public void set(String index, Set<Value> objects) {
		if (index == null || objects == null) return;
		if (Skungee.getConfig().getBoolean("NetworkVariables.AutomaticSharing", false)) {
			if (!ServerTracker.isEmpty()) {
				BungeeSockets.sendAll(new BungeePacket(false, BungeePacketType.UPDATEVARIABLES, index, objects));
			}
		}
		if (variables.containsKey(index)) {
			if (!loadingHash) {
				if (Skungee.getConfig().getBoolean("NetworkVariables.AllowOverrides", true)) {
					try {
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					variables.remove(index);
					loadFromHash();
				} else {
					return;
				}
			}
		}
		variables.put(index, objects);
		try {
			writer.append(index);
			writer.append(DELIMITER);
			writer.append(gson.toJson(objects));
			writer.append("\n");
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
	
	private void loadFromHash() {
		loadingHash = true;
		try {
			writer = new FileWriter(file);
			header();
			if (!variables.isEmpty()) {
				Iterator<String> iterator = variables.keySet().iterator();
				while (iterator.hasNext()) {
					String ID = iterator.next();
					set(ID, variables.get(ID));
				}
			}
		} catch (IOException e) {
			Skungee.exception(e, "Error flushing writer while loading from hash!");
		} finally {
			try {
				writer.flush();
			} catch (IOException e) {
				Skungee.exception(e, "Error flushing writer while loading from hash!");
			}
		}
		loadingHash = false;
	}
}