package me.limeglass.skungee.objects.events;

import java.util.Arrays;

import org.bukkit.event.Cancellable;

import me.limeglass.skungee.objects.SkungeePlayer;

public class SkungeePlayerCommandEvent extends SkungeePlayerEvent implements Cancellable {

	private boolean cancelled;
	private String command;
	
	public SkungeePlayerCommandEvent(String message, String server, SkungeePlayer... players) {
		super(server, players);
		this.command = message;
	}
	
	public String getCommand() {
		return command.split(" ")[0];
	}
	
	public String[] getArguments() {
		String[] arguments = command.split(" ");
		return Arrays.copyOfRange(arguments, 1, arguments.length);
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}

}
