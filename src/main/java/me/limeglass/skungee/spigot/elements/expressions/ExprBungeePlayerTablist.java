package me.limeglass.skungee.spigot.elements.expressions;

import java.util.Set;
import org.bukkit.event.Event;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.skungee.objects.SkriptChangeMode;
import me.limeglass.skungee.objects.SkungeePacket;
import me.limeglass.skungee.objects.SkungeePacketType;
import me.limeglass.skungee.spigot.lang.SkungeePropertyExpression;
import me.limeglass.skungee.spigot.sockets.Sockets;
import me.limeglass.skungee.spigot.utils.Utils;
import me.limeglass.skungee.spigot.utils.annotations.AllChangers;
import me.limeglass.skungee.spigot.utils.annotations.Properties;
import me.limeglass.skungee.spigot.utils.annotations.PropertiesAddition;

@Name("Bungeecord player displayname")
@Description("Returns the display name(s) of the defined Bungeecord player(s).")
@Properties({"strings/players", "bungee[[ ]cord] display[ ]name[s]", "{1}[(all [[of] the]|the)]"})
@PropertiesAddition("[(player|uuid)[s]]")
@AllChangers
public class ExprBungeePlayerTablist extends SkungeePropertyExpression<Object, String> {

	@Override
	protected String[] get(Event event, Object[] skungeePlayers) {
		if (isNull(event)) return null;
		@SuppressWarnings("unchecked")
		Set<String> names = (Set<String>) Sockets.send(new SkungeePacket(true, SkungeePacketType.PLAYERDISPLAYNAME, Utils.toSkungeePlayers(skungeePlayers)));
		return (names != null) ? names.toArray(new String[names.size()]) : null;
	}
	
	@Override
	public void change(Event event, Object[] delta, ChangeMode mode) {
		SkriptChangeMode changer = Utils.getEnum(SkriptChangeMode.class, mode.toString());
		if (isNull(event) || delta == null || changer == null) return;
		Sockets.send(new SkungeePacket(false, SkungeePacketType.PLAYERDISPLAYNAME, (String) delta[0], null, changer, Utils.toSkungeePlayers(getExpr().getAll(event))));
	}
}