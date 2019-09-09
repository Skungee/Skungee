package me.limeglass.skungee.bungeecord.variables;

import java.io.File;
import java.util.TreeMap;

import me.limeglass.skungee.bungeecord.Skungee;
import me.limeglass.skungee.objects.SkungeeVariable.Value;

public abstract class SkungeeStorage {

	protected final static String variablesFolder = Skungee.getInstance().getDataFolder().getAbsolutePath() + File.separator + "variables" + File.separator;
	protected final static TreeMap<String, Value[]> variables = new TreeMap<String, Value[]>(String.CASE_INSENSITIVE_ORDER);
	private final String[] names;

	public SkungeeStorage(String... names) {
		this.names = names;
	}

	public int getSize() {
		return variables.size();
	}

	protected static void registerStorage(SkungeeStorage storage) {
		VariableManager.registerStorage(storage);
	}

	public String[] getNames() {
		return names;
	}

	public abstract void remove(Value[] objects, String... index);

	public abstract void set(String index, Value[] objects);

	public abstract void delete(String... index);

	public abstract Value[] get(String index);

	/**
	 * @returns true if initialization was successful.
	 */
	protected abstract boolean initialize();

	/**
	 * When a backup is called to be processed based on the configuration time.
	 */
	protected abstract void backup();

}
