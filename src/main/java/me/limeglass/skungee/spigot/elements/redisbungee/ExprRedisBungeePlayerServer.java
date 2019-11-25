package me.limeglass.skungee.spigot.elements.redisbungee;

import java.util.Set;

import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import me.limeglass.skungee.spigot.lang.SkungeePropertyExpression;
import me.limeglass.skungee.spigot.utils.Utils;
import me.limeglass.skungee.spigot.utils.annotations.Properties;
import me.limeglass.skungee.spigot.utils.annotations.PropertiesAddition;

@Name("RedisBungee player server")
@Description("Returns the server(s) of the defined RedisBungee player(s).")
@Properties({"strings/players", "[(connected|current)] redis[( |-)]bungee[[ ]cord] server[s]", "{1}[(all [[of] the]|the)]"})
@PropertiesAddition("[(player|uuid)[s]]")
public class ExprRedisBungeePlayerServer extends SkungeePropertyExpression<Object, String> {

	@Override
	protected String[] get(Event event, Object[] skungeePlayers) {
		if (isNull(event))
			return null;
		@SuppressWarnings("unchecked")
		Set<String> servers = (Set<String>) sockets.send(new SkungeePacket(true, SkungeePacketType.REDISPLAYERSERVER, Utils.toSkungeePlayers(skungeePlayers)));
		return (servers != null) ? servers.toArray(new String[servers.size()]) : null;
	}

}
