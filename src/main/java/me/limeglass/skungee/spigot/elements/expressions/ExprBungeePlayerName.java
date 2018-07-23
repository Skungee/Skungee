package me.limeglass.skungee.spigot.elements.expressions;

import java.util.Set;
import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import me.limeglass.skungee.spigot.lang.SkungeePropertyExpression;
import me.limeglass.skungee.spigot.sockets.Sockets;
import me.limeglass.skungee.spigot.utils.Utils;
import me.limeglass.skungee.spigot.utils.annotations.Properties;
import me.limeglass.skungee.spigot.utils.annotations.PropertiesAddition;

@Name("Bungeecord player name")
@Description("Returns the name(s) of the defined Bungeecord player(s).")
@Properties({"strings/players", "bungee[[ ]cord] [user[ ]]name[s]", "{1}[(all [[of] the]|the)]"})
@PropertiesAddition("[(player|uuid)[s]]")
public class ExprBungeePlayerName extends SkungeePropertyExpression<Object, String> {

	@Override
	protected String[] get(Event event, Object[] skungeePlayers) {
		if (isNull(event)) return null;
		@SuppressWarnings("unchecked")
		Set<String> names = (Set<String>) Sockets.send(new SkungeePacket(true, SkungeePacketType.PLAYERNAME, Utils.toSkungeePlayers(skungeePlayers)));
		return (names != null) ? names.toArray(new String[names.size()]) : null;
	}
}