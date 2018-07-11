package me.limeglass.skungee.bungeecord.variables;

import me.limeglass.skungee.bungeecord.Skungee;

public class VariableBackup implements Runnable {

	Boolean messages;
	
	public VariableBackup(boolean messages) {
		this.messages = messages;
	}
	
	@Override
	public void run() {
		if (messages) Skungee.consoleMessage("Variables have been saved!");
		VariableManager.backup();
	}
}
