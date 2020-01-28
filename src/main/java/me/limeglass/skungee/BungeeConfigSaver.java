package me.limeglass.skungee;

import java.io.File;
import java.io.IOException;

import com.google.common.io.Files;

import me.limeglass.skungee.bungeecord.Skungee;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class BungeeConfigSaver {

	//TODO Make a saver that saves the contents into a new configuration.

	private final File folder, config, oldconfig;
	private Configuration configuration;

	public BungeeConfigSaver(Skungee instance) {
		this.folder = new File(instance.getDataFolder(), "old-configs/");
		this.config = new File(instance.getDataFolder(), "config.yml");
		load();
		this.oldconfig = new File(folder, configuration.getString("version", "old") + "-config.yml");
		if (!folder.exists()) folder.mkdir();
	}

	private void load() {
		try {
			configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(config);
		} catch (IOException exception) {
			Skungee.exception(exception, "Failed to load the configuration.");
		}
	}

	public void execute() {
		try {
			Files.move(config, oldconfig);
		} catch (IOException exception) {
			Skungee.exception(exception, "Failed to save the old configuration.");
		}
	}

}
