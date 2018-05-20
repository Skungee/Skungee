package me.limeglass.skungee.spigot.serverinstances.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.ExpressionType;
import me.limeglass.skungee.objects.SkungeePacket;
import me.limeglass.skungee.objects.SkungeePacketType;
import me.limeglass.skungee.spigot.lang.SkungeeExpression;
import me.limeglass.skungee.spigot.sockets.Sockets;
import me.limeglass.skungee.spigot.utils.annotations.ExpressionProperty;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

import java.util.Set;

import org.bukkit.event.Event;

@Name("ServerInstances servers")
@Description("Returns a string list of all the serverinstances.")
@Patterns("[(all [[of] the]|the)] server[ ]instances [names]")
@ExpressionProperty(ExpressionType.SIMPLE)
public class ExprServerInstances extends SkungeeExpression<String> {
	
	@Override
	protected String[] get(Event event) {
		@SuppressWarnings("unchecked")
		Set<String> servers = (Set<String>) Sockets.send(new SkungeePacket(true, SkungeePacketType.SERVERINSTANCES));
		return (servers != null) ? servers.toArray(new String[servers.size()]) : null;
	}
}