package me.limeglass.skungee.spigot.elements.expressions;

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

@Name("Bungeecord event players")
@Description("Returns the Bungeecord player(s) invloved in the event.")
@Patterns("[(all [[of] the]|the)] event (skungee|bungee[[ ]cord]) player[s]")
@ExpressionProperty(ExpressionType.SIMPLE)
@DetermineSingle("players")
@Events({PlayerDisconnectEvent.class, PlayerSwitchServerEvent.class})
public class ExprEventBungeePlayers extends SkungeeExpression<Object> {
	
	@Override
	protected Object[] get(Event event) {
		try {
			Method method = event.getClass().getMethod("getConverted");
			if (method == null) return null;
			method.setAccessible(true);
			return (Object[]) method.invoke(event.getClass());
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {}
		return null;
	}
}
