package me.limeglass.skungee.spigot.elements.bungeetablistplus;

import java.util.Set;

import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import codecrafter47.bungeetablistplus.api.bungee.CustomTablist;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.spigot.lang.SkungeePropertyExpression;
import me.limeglass.skungee.spigot.utils.annotations.Disabled;
import me.limeglass.skungee.spigot.utils.annotations.Properties;
import me.limeglass.skungee.spigot.utils.annotations.PropertiesAddition;

@Name("BungeeTabListPlus - Tablist rows")
@Description("Returns the amount of rows for the defined CustomTablist(s).")
@Properties({"customtablist", "[(size|amount|number) of] rows", "{1}[(all [[of] the]|the)]"})
@PropertiesAddition("[bungee[[ ]tab[list]][[ ]plus] [tab[ ]]list[s]]")
@Disabled
public class ExprTablistRows extends SkungeePropertyExpression<CustomTablist, Number> {

	@Override
	protected Number[] get(Event event, CustomTablist[] tablists) {
		if (isNull(event))
			return null;
		ServerPacket packet = new ServerPacket(true, ServerPacketType.BTLP_TABLISTROWS, tablists);
		@SuppressWarnings("unchecked")
		Set<Number> rows = (Set<Number>) sockets.send(packet);
		return (rows != null) ? rows.toArray(new Number[rows.size()]) : null;
	}

}
