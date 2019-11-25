package me.limeglass.skungee.spigot.elements.expressions;

import java.util.Set;

import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.ExpressionType;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import me.limeglass.skungee.spigot.lang.SkungeeExpression;
import me.limeglass.skungee.spigot.utils.annotations.ExpressionProperty;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

@Name("Bungeecord servers")
@Description("Returns a string list of all the bungeecord servers.")
@Patterns("[(all [[of] the]|the)] [connected] bungee[[ ]cord] servers")
@ExpressionProperty(ExpressionType.SIMPLE)
public class ExprBungeeServers extends SkungeeExpression<String> {

	@Override
	protected String[] get(Event event) {
		@SuppressWarnings("unchecked")
		Set<String> servers = (Set<String>) sockets.send(new SkungeePacket(true, SkungeePacketType.ALLSERVERS));
		return (servers != null) ? servers.toArray(new String[servers.size()]) : null;
	}

}
