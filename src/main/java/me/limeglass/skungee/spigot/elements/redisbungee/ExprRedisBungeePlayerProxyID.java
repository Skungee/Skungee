package me.limeglass.skungee.spigot.elements.redisbungee;

import java.util.Set;

import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.spigot.lang.SkungeePropertyExpression;
import me.limeglass.skungee.spigot.utils.Utils;
import me.limeglass.skungee.spigot.utils.annotations.Properties;
import me.limeglass.skungee.spigot.utils.annotations.PropertiesAddition;

@Name("RedisBungee player proxy ID")
@Description("Returns the ID(s) of the Bungeecord proxy that the defined RedisBungee player(s) are on.")
@Properties({"strings/players", "redis[( |-)]bungee[[ ]cord] [player] [proxy] ID", "{1}[(all [[of] the]|the)]"})
@PropertiesAddition("[(player|uuid)[s]]")
public class ExprRedisBungeePlayerProxyID extends SkungeePropertyExpression<Object, String> {

	@Override
	protected String[] get(Event event, Object[] skungeePlayers) {
		if (isNull(event))
			return null;
		@SuppressWarnings("unchecked")
		Set<String> IDS = (Set<String>) sockets.send(new ServerPacket(true, ServerPacketType.REDISPLAYERID, Utils.toSkungeePlayers(skungeePlayers)));
		return (IDS != null) ? IDS.toArray(new String[IDS.size()]) : null;
	}

}
