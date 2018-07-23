package me.limeglass.skungee.bungeecord.handlers;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import me.limeglass.skungee.bungeecord.Skungee;
import me.limeglass.skungee.bungeecord.handlercontroller.SkungeeBungeeHandler;
import me.limeglass.skungee.objects.SkungeeEnums.SkriptChangeMode;
import me.limeglass.skungee.objects.SkungeeEnums.State;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import me.limeglass.skungee.objects.packets.SkungeeYamlPacket;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class YamlHandler extends SkungeeBungeeHandler {
	
	static {
		registerPacket(new YamlHandler(), SkungeePacketType.YAML);
	}
	
	private Object[] get(Configuration configuration, String node, State state) {
		if (state == State.VALUE) {
			return new Object[] {configuration.get(node)};
		} else if (state == State.NODES) {
			Collection<String> nodes = configuration.getKeys();
			if (nodes == null) return null;
			return nodes.toArray(new String[nodes.size()]);
		} else if (state == State.LIST) {
			List<?> items = configuration.getList(node);
			return items.toArray();
		}
		return null;
	}

	@Override
	public Object[] handlePacket(SkungeePacket input, InetAddress address) {
		SkungeeYamlPacket packet = (SkungeeYamlPacket) input;
		if (packet.isUnset()) return null; //node, path or state could be null.
		String path = packet.getPath(), node = packet.getNode();
		SkriptChangeMode mode = packet.getChangeMode();
		Configuration configuration = null;
		State state = packet.getState();
		File file = null;
		try {
			if (!path.endsWith(".yml")) path = path + ".yml";
			if (path.contains("/")) path.replace("/", File.separator);
			file = new File(path);
			if (!file.exists()) createFileAndPath(file);
			configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
			if (configuration.get(node) == null && mode == null) {
				Skungee.consoleMessage("The file \"" + path + "\" does not contain the node \"" + node + "\"");
				return null;
			}
			if (!configuration.getKeys().contains(node) && (mode != null && mode == SkriptChangeMode.REMOVE)) {
				Skungee.consoleMessage("The file \"" + path + "\" does not contain the node \"" + node + "\"");
				return null;
			}
		} catch (IOException error) {
			Skungee.exception(error, "There was an error attempting to grab the yaml node \"" + node + "\" from " + path);
			return null;
		}
		if (mode == null) {
			return get(configuration, node, state);
		} else {
			List<Object> collection = new ArrayList<Object>();
			Object[] delta = packet.getDelta();
			switch (mode) {
				case REMOVE:
					collection.addAll(configuration.getList(node));
					for (Object object : delta) {
						collection.remove(parse(object));
					}
					configuration.set(node, collection);
					break;
				case REMOVE_ALL:
				case DELETE:
				case RESET:
					configuration.set(node, null);
					break;
				case ADD:
					for (Object object : delta) {
						collection.add(parse(object));
					}
					if (configuration.getKeys().contains(path)) collection.addAll(configuration.getList(node));
					configuration.set(node, collection);
					break;
				case SET:
					configuration.set(node, delta.length == 1 ? delta[0] : delta);
					break;
			}
			if (file != null)
				try {
					ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, file);
				} catch (IOException e) {
					Skungee.consoleMessage("There was an error attempting to save configuration at " + file.getPath());
				}
		}
		return null;
	}
	
	private Object parse(Object delta) {
		if (String.class.isAssignableFrom(delta.getClass())) {
			String s = ((String) delta);
			if (s.matches("true|false|yes|no|on|off")) {
				return s.matches("true|yes|on");
			} else if (s.matches("(-)?\\d+")) {
				return Long.parseLong(s);
			} else if (s.matches("(-)?\\d+(\\.\\d+)")) {
				return Double.parseDouble(s);
			} else {
				return s;
			}
		}
		return delta;
	}
	
	public static void createFileAndPath(File file) throws IOException {
		if (!file.exists()) {
			@SuppressWarnings("unused")
			String folderPath;
			File folder;
			String filePath = file.getPath();
			int index = filePath.lastIndexOf(File.separator);
			if (index >= 0 && !(folder = new File(folderPath = filePath.substring(0, index))).exists()) {
				folder.mkdirs();
			}
			file.createNewFile();
		}
	}
}
