package me.limeglass.skungee.common.wrappers;

import java.io.File;
import java.util.List;

import me.limeglass.skungee.common.wrappers.SkungeePlatform.Platform;

/**
 * Configuration wrapper for all platforms.
 */
public interface SkungeeConfiguration {

	/**
	 * Initalization. Can be called when reloading this configuration.
	 */
	public void loadConfiguration();

	/**
	 * @return Which proxy platform this configuration is for.
	 */
	public Platform getPlatform();

	/**
	 * @return if debug mode is enabled.
	 */
	public boolean debug();

	File getFile();

	SecurityConfiguration getSecurityConfiguration();

	public List<String> getIgnoredDebugPackets();

}
