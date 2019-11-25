package me.limeglass.skungee.spigot.elements.expressions;

import java.util.Collection;

import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.ExpressionType;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import me.limeglass.skungee.spigot.lang.SkungeeExpression;
import me.limeglass.skungee.spigot.utils.annotations.ExpressionProperty;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

@Name("Bungeecord disabled commands")
@Description("Returns all of the disabled commands that are located within the Bungeecord configuration.")
@Patterns({"[(all [[of] the]|the)] bungee[[ ]cord] disabled commands", "bungee[[ ]cord]'s disabled commands", "[(all [[of] the]|the)] disabled commands (on|of|from) [the] bungee[[ ]cord]"})
@ExpressionProperty(ExpressionType.PROPERTY)
public class ExprBungeeDisabledCommands extends SkungeeExpression<String> {

	@Override
	@Nullable
	protected String[] get(Event event) {
		if (areNull(event))
			return null;
		@SuppressWarnings("unchecked")
		Collection<String> commands = (Collection<String>) sockets.send(new SkungeePacket(true, SkungeePacketType.DISABLEDCOMMANDS));
		return (commands != null) ? commands.toArray(new String[commands.size()]) : null;
	}

}
