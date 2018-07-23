package me.limeglass.skungee.spigot.elements.bungeetablistplus;

import java.util.Set;

import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import codecrafter47.bungeetablistplus.api.bungee.CustomTablist;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import me.limeglass.skungee.spigot.lang.SkungeePropertyExpression;
import me.limeglass.skungee.spigot.sockets.Sockets;
import me.limeglass.skungee.spigot.utils.annotations.Disabled;
import me.limeglass.skungee.spigot.utils.annotations.Properties;
import me.limeglass.skungee.spigot.utils.annotations.PropertiesAddition;

@Name("BungeeTabListPlus - Tablist columns")
@Description("Returns the amount of column for the defined CustomTablist(s).")
@Properties({"customtablist", "[(size|amount|number) of] columns", "{1}[(all [[of] the]|the)]"})
@PropertiesAddition("[bungee[[ ]tab[list]][[ ]plus] [tab[ ]]list[s]]")
@Disabled
public class ExprTablistColumns extends SkungeePropertyExpression<CustomTablist, Number> {

	@Override
	protected Number[] get(Event event, CustomTablist[] tablists) {
		if (isNull(event)) return null;
		@SuppressWarnings("unchecked")
		Set<Number> columns = (Set<Number>) Sockets.send(new SkungeePacket(true, SkungeePacketType.BTLP_TABLISTCOLUMNS, tablists));
		return (columns != null) ? columns.toArray(new Number[columns.size()]) : null;
	}
}