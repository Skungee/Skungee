package me.limeglass.skungee.spigot.elements.bungeetablistplus;

import java.util.Set;

import org.bukkit.event.Event;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import codecrafter47.bungeetablistplus.api.bungee.CustomTablist;
import me.limeglass.skungee.objects.SkriptChangeMode;
import me.limeglass.skungee.objects.SkungeePacket;
import me.limeglass.skungee.objects.SkungeePacketType;
import me.limeglass.skungee.spigot.lang.SkungeePropertyExpression;
import me.limeglass.skungee.spigot.sockets.Sockets;
import me.limeglass.skungee.spigot.utils.Utils;
import me.limeglass.skungee.spigot.utils.annotations.AllChangers;
import me.limeglass.skungee.spigot.utils.annotations.Properties;
import me.limeglass.skungee.spigot.utils.annotations.PropertiesAddition;

@Name("BungeeTabListPlus - Tablist header")
@Description("Returns the header of the defined CustomTablist(s), may also be used with changers.")
@Properties({"customtablist", "header", "{1}[(all [[of] the]|the)]"})
@PropertiesAddition("[bungee[[ ]tab[list][[ ]plus]] tab[[ ]list[s]]")
@AllChangers
public class ExprTablistHeader extends SkungeePropertyExpression<CustomTablist, String> {

	@Override
	protected String[] get(Event event, CustomTablist[] tablists) {
		if (isNull(event)) return null;
		@SuppressWarnings("unchecked")
		Set<String> headers = (Set<String>) Sockets.send(new SkungeePacket(true, SkungeePacketType.BTLP_TABLISTHEADER, tablists));
		return (headers != null) ? headers.toArray(new String[headers.size()]) : null;
	}
	
	@Override
	public void change(Event event, Object[] delta, ChangeMode mode) {
		SkriptChangeMode changer = Utils.getEnum(SkriptChangeMode.class, mode.toString());
		if (isNull(event) || delta == null || changer == null) return;
		Sockets.send(new SkungeePacket(false, SkungeePacketType.BTLP_TABLISTHEADER, delta, getExpr().getAll(event), changer));
	}
}