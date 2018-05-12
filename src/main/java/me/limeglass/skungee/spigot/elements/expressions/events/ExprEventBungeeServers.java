package me.limeglass.skungee.spigot.elements.expressions.events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.ExpressionType;
import me.limeglass.skungee.objects.events.PlayerDisconnectEvent;
import me.limeglass.skungee.objects.events.PlayerSwitchServerEvent;
import me.limeglass.skungee.spigot.lang.SkungeeExpression;
import me.limeglass.skungee.spigot.utils.annotations.DetermineSingle;
import me.limeglass.skungee.spigot.utils.annotations.Events;
import me.limeglass.skungee.spigot.utils.annotations.ExpressionProperty;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

@Name("Bungeecord event servers")
@Description("Returns the Bungeecord server(s) invloved in the event.")
@Patterns("[(all [[of] the]|the)] event (skungee|bungee[[ ]cord]) server[s]")
@ExpressionProperty(ExpressionType.SIMPLE)
@DetermineSingle("servers")
@Events({PlayerDisconnectEvent.class, PlayerSwitchServerEvent.class})
public class ExprEventBungeeServers extends SkungeeExpression<String> {
	
	//TODO get rid of the other stuff thing ya
	
	@Override
	protected String[] get(Event event) {
		try {
			Method method = (isSingle()) ? event.getClass().getDeclaredMethod("getServer") : event.getClass().getDeclaredMethod("getServers");
			if (method == null) return null;
			method.setAccessible(true);
			String[] servers = getServers(method.invoke(event));
			return servers;
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {}
		return null;
	}
	
	private String[] getServers(Object servers) {
		if (servers instanceof String) {
			return getServers_i((String)servers);
		}
		return getServers_i((String[])servers);
	}
	
	private String[] getServers_i(String... servers) {
		return servers;
	}
}