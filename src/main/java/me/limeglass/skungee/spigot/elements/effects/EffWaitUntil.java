package me.limeglass.skungee.spigot.elements.effects;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.util.Timespan;
import me.limeglass.skungee.spigot.SkungeeSpigot;
import me.limeglass.skungee.spigot.lang.SkungeeEffect;
import me.limeglass.skungee.spigot.utils.Utils;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

@Name("Wait until connection")
@Description("Waits until Skungee has connected to the Bungeecord Skungee.")
@Patterns("wait [asynchronously] until [skungee] (connect(s|[ion])|[has] connect(ed|[ion])) [[with] timeout %-timespan%]")
public class EffWaitUntil extends SkungeeEffect {
	
	@Override
	protected TriggerItem walk(Event event) {
		long timeout = 50000; //about 4 minutes
		Timespan timespan = expressions.getSingle(event, Timespan.class);
		if (timespan != null) timeout = Utils.getTicks(timespan);
		check(event, timeout);
		return null;
	}
	
	private void check(Event event, long timeout) {
		if (timeout == 0 || sockets.isConnected()) {
			walk(getNext(), event);
			return;
		}
		Bukkit.getScheduler().runTaskAsynchronously(SkungeeSpigot.getInstance(), () -> check(event, timeout - 1));
	}
	
	@Override
	protected void execute(Event event) {}

}
