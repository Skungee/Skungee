package me.limeglass.skungee.bungeecord.serverinstances;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import me.limeglass.skungee.bungeecord.Skungee;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class Premium {
	
	public static Boolean check() {
		Configuration yaml = getBungeeYAML();
		if (yaml != null) {
			if (yaml.getString("name", "null").equals("ServerInstances")) {
				if (yaml.getString("author", "null").equals("LimeGlass")) {
					ProxyServer.getInstance().getPluginManager().detectPlugins(Skungee.getInstance().getDataFolder());
					return true;
				}
			}
		}
		return false;
	}

	public static Configuration getBungeeYAML() {
		for (File file : Skungee.getInstance().getDataFolder().listFiles()) {
			if (file.isFile() && file.getName().endsWith(".jar")) {
				try {
					@SuppressWarnings("resource")
					ZipFile zip = new ZipFile(file);
					ZipEntry entry = zip.getEntry("bungee.yml");
					if (entry != null) {
						return ConfigurationProvider.getProvider(YamlConfiguration.class).load(zip.getInputStream(entry));
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}	
}
