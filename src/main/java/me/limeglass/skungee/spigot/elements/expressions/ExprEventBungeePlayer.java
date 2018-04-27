package me.limeglass.skungee.spigot.elements.expressions;

import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.ExpressionType;
import me.limeglass.skungee.objects.events.BungeecordEvent;
import me.limeglass.skungee.objects.events.EvtPlayerDisconnect;
import me.limeglass.skungee.objects.events.EvtPlayerSwitchServer;
import me.limeglass.skungee.spigot.lang.SkungeeExpression;
import me.limeglass.skungee.spigot.utils.annotations.Events;
import me.limeglass.skungee.spigot.utils.annotations.ExpressionProperty;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;
import me.limeglass.skungee.spigot.utils.annotations.Single;

@Name("Bungeecord event player")
@Description("Returns the Bungeecord player invloved in the event.")
@Patterns("[the] (skungee|bungee[[ ]cord]) player")
@ExpressionProperty(ExpressionType.SIMPLE)
@Single
@Events({EvtPlayerDisconnect.class, EvtPlayerSwitchServer.class})
public class ExprEventBungeePlayer extends SkungeeExpression<Object> {
	
	@Override
	protected Object[] get(Event event) {
		return (((BungeecordEvent)event).getPlayer() != null) ? ((BungeecordEvent)event).getConverted() : null;
	}
}