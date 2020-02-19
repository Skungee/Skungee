package me.limeglass.skungee.spigot;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.io.Files;

public class SpigotConfigSaver {

	//TODO Make a saver that saves the contents into a new configuration.

	private final File folder, config, oldconfig;
	private FileConfiguration configuration;

	public SpigotConfigSaver(SkungeeSpigot instance) {
		this.folder = new File(instance.getDataFolder(), "old-configs/");
		this.config = new File(instance.getDataFolder(), "config.yml");
		load();
		this.oldconfig = new File(folder, configuration.getString("version", "old") + "-config.yml");
		if (!folder.exists())
			folder.mkdir();
		instance.saveConfig();
	}

	private void load() {
		configuration = new YamlConfiguration();
		try {
			configuration.load(config);
		} catch (IOException | InvalidConfigurationException exception) {
			SkungeeSpigot.exception(exception, "Failed to load the configuration.");
		}
	}

	public void execute() {
		try {
			Files.move(config, oldconfig);
		} catch (IOException exception) {
			SkungeeSpigot.exception(exception, "Failed to save the old configuration.");
		}
	}

}
