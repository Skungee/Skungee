package me.limeglass.skungee.spigot.lang;

import ch.njol.skript.classes.Parser;

public abstract class SkungeeParser<T> extends Parser<T> {

	private String variableNamePattern;

	public SkungeeParser(String variableNamePattern) {
		this.variableNamePattern = variableNamePattern;
	}

	public String getVariableNamePattern() {
		return String.valueOf(this.variableNamePattern) + ":.+";
	}

	public String toVariableNameString(T object) {
		return String.valueOf(this.variableNamePattern) + ":" + " " + object.toString().toLowerCase();
	}

	public String toString(T object, int i) {
		return object.toString().toLowerCase().replace("_", " ");
	}

}
