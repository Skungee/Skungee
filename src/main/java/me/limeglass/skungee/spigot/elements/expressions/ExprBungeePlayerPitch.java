package me.limeglass.skungee.spigot.elements.expressions;

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

@Name("Bungeecord player pitch")
@Description("Returns the pitch orientation(s) of the defined Bungeecord player(s).")
@Properties({"strings/players", "bungee[[ ]cord] pitch[es]", "{1}[(all [[of] the]|the)]"})
@PropertiesAddition("[(player|uuid)[s]]")
public class ExprBungeePlayerPitch extends SkungeePropertyExpression<Object, Number> {

	@Override
	protected Number[] get(Event event, Object[] skungeePlayers) {
		if (isNull(event))
			return null;
		@SuppressWarnings("unchecked")
		Set<Number> pitchs = (Set<Number>) sockets.send(new ServerPacket(true, ServerPacketType.PLAYERPITCH, null, null, Utils.toSkungeePlayers(skungeePlayers)));
		return (pitchs != null) ? pitchs.toArray(new Number[pitchs.size()]) : null;
	}

}
