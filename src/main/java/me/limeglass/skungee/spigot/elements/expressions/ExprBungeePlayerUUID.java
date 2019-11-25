package me.limeglass.skungee.spigot.elements.expressions;

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

@Name("Bungeecord uuid")
@Description("Returns the uuid(s) of the defined Bungeecord player(s). The Bungeecord unique id, is the best option that Bungeecord can find."
		+ "\nIf your spigot server doesn't have the `bungeecord` option set to true and the Bungeecord doesn't have `ip_forward` and `online_mode` true, this will be a generated UUID from Spigot.")
@Properties({"strings/players", "bungee[[ ]cord] (uuid|unique id)[s]", "{1}[(all [[of] the]|the)]"})
@PropertiesAddition("[player[s]]")
public class ExprBungeePlayerUUID extends SkungeePropertyExpression<Object, String> {

	@Override
	protected String[] get(Event event, Object[] skungeePlayers) {
		if (isNull(event))
			return null;
		@SuppressWarnings("unchecked")
		Set<String> uniqueIds = (Set<String>) sockets.send(new SkungeePacket(true, SkungeePacketType.PLAYERUUID, Utils.toSkungeePlayers(skungeePlayers)));
		return (uniqueIds != null) ? uniqueIds.toArray(new String[uniqueIds.size()]) : null;
	}

}
