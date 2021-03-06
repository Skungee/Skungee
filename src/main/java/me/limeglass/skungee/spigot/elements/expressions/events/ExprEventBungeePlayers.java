package me.limeglass.skungee.spigot.elements.expressions.events;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.ExpressionType;
import me.limeglass.skungee.objects.Returnable;
import me.limeglass.skungee.objects.SkungeePlayer;
import me.limeglass.skungee.objects.events.SkungeePlayerDisconnect;
import me.limeglass.skungee.objects.events.SkungeePlayerEvent;
import me.limeglass.skungee.objects.events.SkungeePlayerSwitchServer;
import me.limeglass.skungee.spigot.lang.SkungeeExpression;
import me.limeglass.skungee.spigot.utils.annotations.Events;
import me.limeglass.skungee.spigot.utils.annotations.ExpressionProperty;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

@Name("Bungeecord event players")
@Description("Returns the Bungeecord player(s) invloved in the event.")
@Patterns("[(all [[of] the]|the)] event (skungee|bungee[[ ]cord]) player[s]")
@ExpressionProperty(ExpressionType.SIMPLE)
@Events({SkungeePlayerDisconnect.class, SkungeePlayerSwitchServer.class})
public class ExprEventBungeePlayers extends SkungeeExpression<String> implements Returnable {
	
	@Override
	protected String[] get(Event event) {
		Set<String> names = new HashSet<>();
		for (SkungeePlayer player : ((SkungeePlayerEvent) event).getPlayers()) {
			if (player != null)
				names.add(player.getName());
		}
		return names.toArray(new String[names.size()]);
	}

}
