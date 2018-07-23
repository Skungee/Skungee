package me.limeglass.skungee.spigot.elements.bungeetablistplus;

import java.util.Set;

import org.bukkit.event.Event;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import codecrafter47.bungeetablistplus.api.bungee.CustomTablist;
import me.limeglass.skungee.objects.SkungeeEnums.SkriptChangeMode;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import me.limeglass.skungee.spigot.lang.SkungeePropertyExpression;
import me.limeglass.skungee.spigot.sockets.Sockets;
import me.limeglass.skungee.spigot.utils.Utils;
import me.limeglass.skungee.spigot.utils.annotations.Changers;
import me.limeglass.skungee.spigot.utils.annotations.Disabled;
import me.limeglass.skungee.spigot.utils.annotations.Properties;
import me.limeglass.skungee.spigot.utils.annotations.PropertiesAddition;

@Name("BungeeTabListPlus - Player tablist")
@Description("Returns a players CustomTablist or set the players CustomTablist.")
@Properties({"strings/players", "bungee[[ ]tab[list]][[ ]plus] [tab[ ]]list", "{1}[(all [[of] the]|the)]"})
@PropertiesAddition("[(player|uuid)[s]]")
@Changers({ChangeMode.SET, ChangeMode.DELETE, ChangeMode.RESET})
@Disabled
public class ExprPlayerTablist extends SkungeePropertyExpression<Object, CustomTablist> {

	@Override
	protected CustomTablist[] get(Event event, Object[] skungeePlayers) {
		if (isNull(event)) return null;
		@SuppressWarnings("unchecked")
		Set<CustomTablist> tablists = (Set<CustomTablist>) Sockets.send(new SkungeePacket(true, SkungeePacketType.BTLP_PLAYERTABLIST, Utils.toSkungeePlayers(skungeePlayers)));
		return (tablists != null) ? tablists.toArray(new CustomTablist[tablists.size()]) : null;
	}
	
	@Override
	public void change(Event event, Object[] delta, ChangeMode mode) {
		SkriptChangeMode changer = Utils.getEnum(SkriptChangeMode.class, mode.toString());
		if (isNull(event) || delta == null || changer == null) return;
		Sockets.send(new SkungeePacket(false, SkungeePacketType.BTLP_PLAYERTABLIST, delta, null, changer, Utils.toSkungeePlayers(getExpr().getAll(event))));
	}
}