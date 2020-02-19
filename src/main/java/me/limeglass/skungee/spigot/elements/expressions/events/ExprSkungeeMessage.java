package me.limeglass.skungee.spigot.elements.expressions.events;

import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.ExpressionType;
import me.limeglass.skungee.spigot.events.SkungeeMessageEvent;
import me.limeglass.skungee.spigot.lang.SkungeeExpression;
import me.limeglass.skungee.spigot.utils.annotations.DetermineSingle;
import me.limeglass.skungee.spigot.utils.annotations.Events;
import me.limeglass.skungee.spigot.utils.annotations.ExpressionProperty;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

@Name("Skungee message values")
@Description("Returns the messages of a Skungee message event.")
@Patterns("[(all [[of] the]|the)] (skungee|bungee[[ ]cord]) message[s]")
@ExpressionProperty(ExpressionType.SIMPLE)
@DetermineSingle("messages")
@Events(SkungeeMessageEvent.class)
public class ExprSkungeeMessage extends SkungeeExpression<String> {
	
	@Override
	protected String[] get(Event event) {
		return ((SkungeeMessageEvent)event).getMessages();
	}
}
