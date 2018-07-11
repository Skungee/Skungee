package me.limeglass.skungee.bungeecord.variables;

import java.util.Set;
import java.util.TreeMap;

import me.limeglass.skungee.objects.SkungeeVariable.Value;

public abstract class SkungeeStorage {

	protected final static TreeMap<String, Set<Value>> variables = new TreeMap<String, Set<Value>>(String.CASE_INSENSITIVE_ORDER);
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
	
	public abstract Set<Value> get(String index);
	
	public abstract void set(String index, Set<Value> objects);
	
	public abstract void remove(String index);
	
	//Return true if initialization was successful.
	protected abstract boolean initialize();
	
	//When a backup is called to be processed based on the configuration time.
	protected abstract void backup();
}
