package me.limeglass.skungee.spigot.elements.expressions;

import java.util.Set;

import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.skungee.objects.SkungeePacket;
import me.limeglass.skungee.objects.SkungeePacketType;
import me.limeglass.skungee.spigot.lang.SkungeePropertyExpression;
import me.limeglass.skungee.spigot.sockets.Sockets;
import me.limeglass.skungee.spigot.utils.annotations.Properties;
import me.limeglass.skungee.spigot.utils.annotations.PropertiesAddition;

@Name("Bungeecord server address")
@Description("Returns the ip address(es) of the defined server(s).")
@Properties({"strings", "[server] ip[s] [address[es]]", "{1}[(all [[of] the]|the)]"})
@PropertiesAddition("[bungee[[ ]cord]] server[s]")
public class ExprBungeeServerAddress extends SkungeePropertyExpression<String, String> {
	
	@Override
	protected String[] get(Event event, String[] servers) {
		if (isNull(event)) return null;
		@SuppressWarnings("unchecked")
		Set<String> addresses = (Set<String>) Sockets.send(new SkungeePacket(true, SkungeePacketType.SERVERIP, servers));
		return (addresses != null) ? addresses.toArray(new String[addresses.size()]) : null;
	}
}