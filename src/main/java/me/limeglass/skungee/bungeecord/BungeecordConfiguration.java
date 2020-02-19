package me.limeglass.skungee.bungeecord;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import me.limeglass.skungee.common.wrappers.ProxyConfiguration;
import me.limeglass.skungee.common.wrappers.SecurityConfiguration;
import me.limeglass.skungee.common.wrappers.SkungeePlatform.Platform;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class BungeecordConfiguration implements ProxyConfiguration {

	private final BungeeSecurityConfiguration security = new BungeeSecurityConfiguration();
	private final SkungeeBungee instance;
	private Configuration configuration;
	private String version;
	private File file;

	public BungeecordConfiguration(SkungeeBungee instance) {
		this.instance = instance;
		loadConfiguration();
	}

	@Override
	public void loadConfiguration() {
		try {
			version = instance.getDescription().getVersion();
			file = new File(instance.getDataFolder(), "config.yml");
			if (file.exists()) {
				configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
				if (!version.equals(configuration.getString("version", version))) {
					File folder = new File(instance.getDataFolder(), "old-configs/");
					if (!folder.exists())
						folder.mkdir();
					File relocate = new File(folder, configuration.getString("version", "old") + "-config.yml");
					Files.move(file.toPath(), relocate.toPath());
					file.delete();
					loadConfiguration();
				}
			} else {
				file.getParentFile().mkdirs();
				Files.copy(instance.getResourceAsStream("config.yml"), file.toPath());
				instance.debugMessage("Created new default file " + file.getName());
				configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
			}
			version = configuration.getString("version", version);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean acceptIncomingUUID() {
		return configuration.getBoolean("IncomingUUIDs", true);
	}

	@Override
	public Platform getPlatform() {
		return Platform.BUNGEECORD;
	}

	@Override
	public boolean debug() {
		return configuration.getBoolean("debug", false);
	}

	@Override
	public File getFile() {
		return file;
	}

	@Override
	public boolean shouldDisableTracking() {
		return configuration.getBoolean("tracker.disable-tracking", false);
	}

	@Override
	public int getTrackerTimeout() {
		return configuration.getInt("tracker.allowed-trys", 5);
	}

	@Override
	public int getReceiverTimeout() {
		return configuration.getInt("receivers.allowed-trys", 5);
	}

	@Override
	public SecurityConfiguration getSecurityConfiguration() {
		return security;
	}

	@Override
	public int getBufferSize() {
		return configuration.getInt("buffer-size", 10240);
	}

	@Override
	public String getGlobalScriptsCharset() {
		return configuration.getString("global-scripts.charset", "default");
	}

	@Override
	public boolean areGlobalScriptsEnabled() {
		return configuration.getBoolean("global-scripts.enabled", true);
	}

	@Override
	public List<String> getIgnoredDebugPackets() {
		return configuration.getStringList("debug-ignored-packets");
	}

	@Override
	public boolean isPingEventDisabled() {
		return configuration.getBoolean("DisablePingEvent", false);
	}

	@Override
	public String getConfigurationVersion() {
		return version;
	}

	@Override
	public String getBindAddress() {
		return configuration.getString("bind-to-address", "localhost");
	}

	@Override
	public int getPort() {
		return configuration.getInt("port", 1337);
	}

	@Override
	public boolean allowsConsoleColour() {
		return configuration.getBoolean("console-colour", true);
	}

	@Override
	public boolean shouldAcceptIncomingUUID() {
		return configuration.getBoolean("IncomingUUIDs", true);
	}

	@Override
	public boolean allowsPackets() {
		return configuration.getBoolean("packets", true);
	}

	@Override
	public boolean allowsEvents() {
		return configuration.getBoolean("events", true);
	}

	private class BungeeSecurityConfiguration implements SecurityConfiguration {

		@Override
		public boolean breachAddressesAreWhitelist() {
			return configuration.getBoolean("security.breaches.blacklist-is-whitelist", false);
		}

		@Override
		public List<String> getBreachAddresses() {
			return configuration.getStringList("security.breaches.blacklisted");
		}

		@Override
		public boolean shouldBreachesShutdown() {
			return configuration.getBoolean("security.breaches.shutdown", false);
		}

		@Override
		public String getCipherAlgorithm() {
			return configuration.getString("security.encryption.cipherAlgorithm", "AES/CTS/PKCS5Padding");
		}

		@Override
		public boolean isPasswordFileHashed() {
			return configuration.getBoolean("security.password.hashFile", false);
		}

		@Override
		public boolean shouldBreachesBlock() {
			return configuration.getBoolean("security.breaches.blockAddress", false);
		}

		@Override
		public String getPasswordAlgorithm() {
			return configuration.getString("security.password.hashAlgorithm", "SHA-256");
		}

		@Override
		public boolean areBreachesEnabled() {
			return configuration.getBoolean("security.breaches.enabled", false);
		}

		@Override
		public boolean isPasswordEnabled() {
			return configuration.getBoolean("security.password.enabled", false);
		}

		@Override
		public boolean isPasswordHashed() {
			return configuration.getBoolean("security.password.hash", false);
		}

		@Override
		public boolean shouldLogBreaches() {
			return configuration.getBoolean("security.breaches.log", false);
		}

		@Override
		public int getMaxBreachAttempts() {
			return configuration.getInt("security.breaches.attempts", 30);
		}

		@Override
		public boolean canPrintErrors() {
			return configuration.getBoolean("security.encryption.printEncryptionErrors", false);
		}

		@Override
		public boolean hasEncryption() {
			return configuration.getBoolean("security.encryption.enabled", false);
		}

		@Override
		public String getCipherKey() {
			return configuration.getString("security.encryption.cipherKey", "insert 16 length");
		}

		@Override
		public String getPassword() {
			return configuration.getString("security.password.password");
		}

		@Override
		public boolean debug() {
			return false;
		}

	}

}
