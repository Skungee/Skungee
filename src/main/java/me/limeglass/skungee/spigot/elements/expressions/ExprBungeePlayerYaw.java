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

@Name("Bungeecord player yaw")
@Description("Returns the yaw orientation(s) of the defined Bungeecord player(s).")
@Properties({"strings/players", "bungee[[ ]cord] yaw[s]", "{1}[(all [[of] the]|the)]"})
@PropertiesAddition("[(player|uuid)[s]]")
public class ExprBungeePlayerYaw extends SkungeePropertyExpression<Object, Number> {

	@Override
	protected Number[] get(Event event, Object[] skungeePlayers) {
		if (isNull(event))
			return null;
		@SuppressWarnings("unchecked")
		Set<Number> yaws = (Set<Number>) sockets.send(new SkungeePacket(true, SkungeePacketType.PLAYERYAW, null, null, Utils.toSkungeePlayers(skungeePlayers)));
		return (yaws != null) ? yaws.toArray(new Number[yaws.size()]) : null;
	}

}
