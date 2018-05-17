package me.limeglass.skungee.objects.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SkungeeMessageEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	private final String[] messages;
	private final String channel;
	
	public SkungeeMessageEvent(String channel, String[] messages) {
		this.channel = channel;
		this.messages = messages;
	}
	
	public String getChannel() {
		return channel;
	}
	
	public String[] getMessages() {
		return messages;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}