package me.limeglass.skungee.objects;

import java.io.Serializable;
import java.util.Arrays;

public class SkungeeVariable implements Serializable {

	private static final long serialVersionUID = 1922196457419635337L;
	//The variable name defined as the network variable on the Spigot side {this::without::brackets::*}
	private final Value[] values;
	private final boolean single;
	private final String name;
	
	public SkungeeVariable() {
		this(false, "used internally by gson", new Value());
	}
	
	public SkungeeVariable(final boolean single, final String name, final Value... values) {
		this.values = values;
		this.single = single;
		this.name = name;
	}
	
	public String getVariableName() {
		return name;
	}
	
	public Value[] getValues() {
		return values;
	}
	
	public boolean isSingle() {
		return single;
	}
	
	public String toString() {
		return "name=" + name + ", " + Arrays.toString(values);
	}

	public final static class Value implements Serializable {

		private static final long serialVersionUID = 1428760897685648784L;
		public String type;
		public byte[] data;
		
		public Value() {
			this("used internally by gson", new byte[0]);
		}
		
		public Value(final String type, final byte[] data) {
			this.type = type;
			this.data = data;
		}
		
		public String toString() {
			return "type=" + type + ", data=" + Arrays.toString(data);
		}
	}
}
