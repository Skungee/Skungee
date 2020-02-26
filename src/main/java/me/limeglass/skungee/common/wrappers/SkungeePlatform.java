package me.limeglass.skungee.common.wrappers;

import java.io.File;

import me.limeglass.skungee.EncryptionUtil;

public interface SkungeePlatform {

	public enum Platform {
		SPIGOT,
		BUNGEECORD,
		VELOCITY,
		ANY_PROXY;
	}

	public void exception(Throwable error, String... info);

	public SkungeeConfiguration getConfiguration();

	public void consoleMessage(String... messages);

	public EncryptionUtil getEncryptionUtil();

	public void debugMessage(String text);

	public String getPlatformVersion();

	public Platform getPlatform();

	public File getDataFolder();

}
