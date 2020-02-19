package me.limeglass.skungee.spigot.elements.expressions.events;

import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.ExpressionType;
import me.limeglass.skungee.bungeecord.events.BungeecordEvent;
import me.limeglass.skungee.common.events.SkungeePlayerDisconnect;
import me.limeglass.skungee.common.events.SkungeePlayerSwitchServer;
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
@Events({SkungeePlayerDisconnect.class, SkungeePlayerSwitchServer.class})
public class ExprEventBungeeServers extends SkungeeExpression<String> {
	
	@Override
	protected String[] get(Event event) {
		return new String[] {((BungeecordEvent) event).getServer()};
	}

}
