package me.limeglass.skungee.spigot.elements.redisbungee;

import java.util.Set;
import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.skungee.objects.SkungeePacket;
import me.limeglass.skungee.objects.SkungeePacketType;
import me.limeglass.skungee.spigot.lang.SkungeePropertyExpression;
import me.limeglass.skungee.spigot.sockets.Sockets;
import me.limeglass.skungee.spigot.utils.Utils;
import me.limeglass.skungee.spigot.utils.annotations.Properties;
import me.limeglass.skungee.spigot.utils.annotations.PropertiesAddition;

@Name("RedisBungee player name")
@Description("Returns the name(s) of the defined RedisBungee player(s).")
@Properties({"strings/players", "redis[( |-)]bungee[[ ]cord] [user[ ]]name[s]", "{1}[(all [[of] the]|the)]"})
@PropertiesAddition("[(player|uuid)[s]]")
public class ExprRedisBungeePlayerNames extends SkungeePropertyExpression<Object, String> {

	@Override
	protected String[] get(Event event, Object[] skungeePlayers) {
		if (isNull(event)) return null;
		@SuppressWarnings("unchecked")
		Set<String> names = (Set<String>) Sockets.send(new SkungeePacket(true, SkungeePacketType.REDISPLAYERNAME, Utils.toSkungeePlayers(skungeePlayers)));
		return (names != null) ? names.toArray(new String[names.size()]) : null;
	}
}