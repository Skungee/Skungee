package me.limeglass.skungee.spigot.elements.expressions;

import java.util.Set;

import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.spigot.lang.SkungeePropertyExpression;
import me.limeglass.skungee.spigot.utils.Utils;
import me.limeglass.skungee.spigot.utils.annotations.AllChangers;
import me.limeglass.skungee.spigot.utils.annotations.Properties;
import me.limeglass.skungee.spigot.utils.annotations.PropertiesAddition;

@Name("Bungeecord player view distance")
@Description("Returns the view distance(s) of the defined Bungeecord player(s).")
@Properties({"strings/players", "bungee[[ ]cord] (render|view) distance[s]", "{1}[(all [[of] the]|the)]"})
@PropertiesAddition("[(player|uuid)[s]]")
@AllChangers
public class ExprBungeePlayerViewDIstance extends SkungeePropertyExpression<Object, Number> {

	@Override
	protected Number[] get(Event event, Object[] skungeePlayers) {
		if (isNull(event))
			return null;
		@SuppressWarnings("unchecked")
		Set<Number> distances = (Set<Number>) sockets.send(new ServerPacket(true, ServerPacketType.PLAYERVIEWDISTANCE, Utils.toSkungeePlayers(skungeePlayers)));
		return (distances != null) ? distances.toArray(new Number[distances.size()]) : null;
	}

}
