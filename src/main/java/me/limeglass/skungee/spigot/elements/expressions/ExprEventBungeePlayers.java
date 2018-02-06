package me.limeglass.skungee.spigot.elements.expressions;

import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.ExpressionType;
import me.limeglass.skungee.objects.events.BungeecordEvent;
import me.limeglass.skungee.objects.events.PlayerDisconnectEvent;
import me.limeglass.skungee.objects.events.PlayerSwitchServerEvent;
import me.limeglass.skungee.spigot.lang.SkungeeExpression;
import me.limeglass.skungee.spigot.utils.annotations.DetermineSingle;
import me.limeglass.skungee.spigot.utils.annotations.Events;
import me.limeglass.skungee.spigot.utils.annotations.ExpressionProperty;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

@Name("Bungeecord event players")
@Description("Returns the Bungeecord player(s) invloved in the event.")
@Patterns("[(all [[of] the]|the)] (skungee|bungee[[ ]cord]) [event] player[s]")
@ExpressionProperty(ExpressionType.SIMPLE)
@DetermineSingle("players")
@Events({PlayerDisconnectEvent.class, PlayerSwitchServerEvent.class})
public class ExprEventBungeePlayers extends SkungeeExpression<Object> {
	
	@Override
	protected Object[] get(Event event) {
		if (((BungeecordEvent)event).getPlayers() == null) return null;
		return ((BungeecordEvent)event).getConverted();
	}
}