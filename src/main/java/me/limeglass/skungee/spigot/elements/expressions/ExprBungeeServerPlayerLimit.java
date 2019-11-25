package me.limeglass.skungee.spigot.elements.expressions;

import java.util.Set;

import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import me.limeglass.skungee.spigot.lang.SkungeePropertyExpression;
import me.limeglass.skungee.spigot.utils.annotations.Properties;
import me.limeglass.skungee.spigot.utils.annotations.PropertiesAddition;

@Name("Bungeecord server player limit")
@Description("Returns the max players the defined server(s) can have based on their server.properies.")
@Properties({"strings", "(max[imum] [amount [of]] players|player limit)", "{1}[(all [[of] the]|the)]"})
@PropertiesAddition("[bungee[[ ]cord]] [server[s]]")
public class ExprBungeeServerPlayerLimit extends SkungeePropertyExpression<String, Number> {

	@Override
	protected Number[] get(Event event, String[] servers) {
		if (isNull(event))
			return null;
		@SuppressWarnings("unchecked")
		Set<Number> limits = (Set<Number>) sockets.send(new SkungeePacket(true, SkungeePacketType.MAXPLAYERS, servers));
		return (limits != null) ? limits.toArray(new Number[limits.size()]) : null;
	}

}
