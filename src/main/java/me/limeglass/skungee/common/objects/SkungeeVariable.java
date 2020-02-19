package me.limeglass.skungee.common.objects;

import java.io.Serializable;
import java.util.Arrays;

public class SkungeeVariable implements Serializable {

	private static final long serialVersionUID = 1922196457419635337L;
	//The variable name defined as the network variable on the Spigot side {this::without::brackets::*}
	private Value[] values;
	private String name;

	public SkungeeVariable() {
		this("used internally by gson", new Value());
	}

	public SkungeeVariable(String name, Value... values) {
		this.values = values;
		this.name = name;
	}

	public String getVariableString() {
		return name;
	}

	public Value[] getValues() {
		return values;
	}

	public String toString() {
		return "name=" + name + ", " + Arrays.toString(values);
	}

	/**
	 * A replica of Skript's Value for Skript variables.
	 * Transfers to Skript's Value later on the Spigot side.
	 */
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

		public boolean isSimilar(Value compare) {
			return compare.type.equals(type) && Arrays.equals(compare.data, data);
		}

	}

}
