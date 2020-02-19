package me.limeglass.skungee.proxy.variables;

import me.limeglass.skungee.Skungee;

public class VariableBackup implements Runnable {

	Boolean messages;
	
	public VariableBackup(boolean messages) {
		this.messages = messages;
	}
	
	@Override
	public void run() {
		if (messages)
			Skungee.getPlatform().consoleMessage("Variables have been saved!");
		VariableManager.backup();
	}
}
