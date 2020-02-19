package me.limeglass.skungee.spigot.elements.expressions;

import java.util.Set;

import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.spigot.lang.SkungeePropertyExpression;
import me.limeglass.skungee.spigot.utils.annotations.Properties;
import me.limeglass.skungee.spigot.utils.annotations.PropertiesAddition;

@Name("Bungeecord server motd")
@Description("Returns the message of the day(s) from the defined server(s).")
@Properties({"strings", "(motd|message of the day)[s]", "{1}[(all [[of] the]|the)]"})
@PropertiesAddition("[bungee[[ ]cord]] [server[s]]")
public class ExprBungeeServerMOTD extends SkungeePropertyExpression<String, String> {

	@Override
	protected String[] get(Event event, String[] servers) {
		if (isNull(event))
			return null;
		@SuppressWarnings("unchecked")
		Set<String> motds = (Set<String>) sockets.send(new ServerPacket(true, ServerPacketType.SERVERMOTD, servers));
		return (motds != null) ? motds.toArray(new String[motds.size()]) : null;
	}

}
