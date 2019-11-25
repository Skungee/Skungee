package me.limeglass.skungee.spigot.elements.expressions;

import java.util.Collection;

import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.ExpressionType;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import me.limeglass.skungee.spigot.lang.SkungeeExpression;
import me.limeglass.skungee.spigot.utils.annotations.ExpressionProperty;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

@Name("Bungeecord plugins")
@Description("Returns a string list of all the bungeecord plugins.")
@Patterns("[(all [[of] the]|the)] bungee[[ ]cord][[']s] plugins")
@ExpressionProperty(ExpressionType.SIMPLE)
public class ExprBungeePlugins extends SkungeeExpression<String> {

	@Override
	protected String[] get(Event event) {
		@SuppressWarnings("unchecked")
		Collection<String> plugins = (Collection<String>) sockets.send(new SkungeePacket(true, SkungeePacketType.PLUGINS));
		return (plugins != null) ? plugins.toArray(new String[plugins.size()]) : null;
	}

}
