package me.limeglass.skungee.objects;

import java.io.Serializable;
import java.util.Arrays;

public class SkungeeVariable implements Serializable {

	private static final long serialVersionUID = 1922196457419635337L;
	public String name;
	public Value value;
	
	public SkungeeVariable(final String name, final Value value) {
		this.name = name;
		this.value = value;
	}
	
	public final static class Value implements Serializable {

		private static final long serialVersionUID = 1428760897685648784L;
		public String type;
		public byte[] data;
		
		public Value(final String type, final byte[] data) {
			this.type = type;
			this.data = data;
		}
		
		public String toString() {
			return "type=" + type + ", data=" + Arrays.toString(data);
		}
	}
	
	public String toString() {
		return "name=" + name + ", value=" + value.toString();
	}
}
