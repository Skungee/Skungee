package me.limeglass.skungee.common.wrappers;

import java.util.List;

public interface SecurityConfiguration {

	public boolean breachAddressesAreWhitelist();

	public List<String> getBreachAddresses();

	public boolean shouldBreachesShutdown();

	public boolean shouldBreachesBlock();

	public boolean isPasswordFileHashed();

	public String getPasswordAlgorithm();

	public boolean areBreachesEnabled();

	public String getCipherAlgorithm();

	public boolean isPasswordEnabled();

	public boolean shouldLogBreaches();

	public int getMaxBreachAttempts();

	public boolean isPasswordHashed();

	public boolean canPrintErrors();

	public boolean hasEncryption();

	public String getCipherKey();

	public String getPassword();

	/**
	 * @return if debug mode is enabled for security.
	 */
	public boolean debug();

}
