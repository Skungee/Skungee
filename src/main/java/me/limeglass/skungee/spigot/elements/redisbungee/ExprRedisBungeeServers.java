package me.limeglass.skungee.spigot.elements.redisbungee;

import java.util.List;
import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.ExpressionType;
import me.limeglass.skungee.objects.SkungeePacket;
import me.limeglass.skungee.objects.SkungeePacketType;
import me.limeglass.skungee.spigot.lang.SkungeeExpression;
import me.limeglass.skungee.spigot.sockets.Sockets;
import me.limeglass.skungee.spigot.utils.annotations.ExpressionProperty;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

@Name("RedisBungee servers")
@Description("Returns a string list of all the RedisBungee servers.")
@Patterns("[(all [[of] the]|the)] redis[( |-)]bungee[[ ]cord] servers")
@ExpressionProperty(ExpressionType.SIMPLE)
public class ExprRedisBungeeServers extends SkungeeExpression<String> {
	
	@Override
	protected String[] get(Event event) {
		@SuppressWarnings("unchecked")
		List<String> servers = (List<String>) Sockets.send(new SkungeePacket(true, SkungeePacketType.REDISSERVERS));
		return (servers != null) ? servers.toArray(new String[servers.size()]) : null;
	}
}