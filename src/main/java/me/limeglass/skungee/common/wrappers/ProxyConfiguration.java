package me.limeglass.skungee.common.wrappers;

import java.util.List;

/**
 * Configuration wrapper for proxy platforms.
 */
public interface ProxyConfiguration extends SkungeeConfiguration {

	public List<String> getIgnoredDebugPackets();

	public boolean shouldAcceptIncomingUUID();

	public boolean areGlobalScriptsEnabled();

	public String getGlobalScriptsCharset();

	public boolean shouldDisableTracking();

	/**
	 * @return The version the configuration is set as.
	 */
	public String getConfigurationVersion();

	public boolean allowsConsoleColour();

	public boolean isPingEventDisabled();

	/**
	 * @return The amount multiplied by that server's heartbeat for allowed fails.
	 */
	public int getTrackerTimeout();

	public int getReceiverTimeout();

	/**
	 * @return The defined address to be bound to.
	 */
	public String getBindAddress();

	public boolean allowsPackets();

	public boolean allowsEvents();

	/**
	 * @return The buffer size of the sockets.
	 */
	public int getBufferSize();

	/**
	 * @return The port to run the main socket on.
	 */
	public int getPort();

}
