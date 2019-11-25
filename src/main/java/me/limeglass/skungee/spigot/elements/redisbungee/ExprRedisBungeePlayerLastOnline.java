package me.limeglass.skungee.spigot.elements.redisbungee;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.util.Timespan;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import me.limeglass.skungee.spigot.Skungee;
import me.limeglass.skungee.spigot.lang.SkungeePropertyExpression;
import me.limeglass.skungee.spigot.utils.Utils;
import me.limeglass.skungee.spigot.utils.annotations.Properties;
import me.limeglass.skungee.spigot.utils.annotations.PropertiesAddition;

@Name("RedisBungee last online")
@Description("Returns the last known online time(s) of the defined RedisBungee player(s).")
@Properties({"strings/players", "redis[( |-)]bungee[[ ]cord] last [known] login[s] [time[s]]", "{1}[(all [[of] the]|the)]"})
@PropertiesAddition("[(player|uuid)[s]]")
public class ExprRedisBungeePlayerLastOnline extends SkungeePropertyExpression<Object, Object> {

	@Override
	protected Object[] get(Event event, Object[] skungeePlayers) {
		if (isNull(event))
			return null;
		@SuppressWarnings("unchecked")
		Set<Number> logins = (Set<Number>) sockets.send(new SkungeePacket(true, SkungeePacketType.REDISLASTLOGIN, Utils.toSkungeePlayers(skungeePlayers)));
		if (Skungee.getInstance().getConfig().getBoolean("Timespans", true)) {
			Set<Timespan> timespans = new HashSet<Timespan>();
			for (Number milliseconds : logins) {
				timespans.add(new Timespan((long) milliseconds));
			}
			return (timespans != null) ? timespans.toArray(new Timespan[timespans.size()]) : null;
		}
		return (logins != null) ? logins.toArray(new Number[logins.size()]) : null;
	}

}
