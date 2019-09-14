package me.limeglass.skungee.spigot.elements.expressions;

import java.util.Set;
import org.bukkit.event.Event;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.skungee.objects.SkungeeEnums.SkriptChangeMode;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import me.limeglass.skungee.spigot.lang.SkungeePropertyExpression;
import me.limeglass.skungee.spigot.sockets.Sockets;
import me.limeglass.skungee.spigot.utils.Utils;
import me.limeglass.skungee.spigot.utils.annotations.Changers;
import me.limeglass.skungee.spigot.utils.annotations.Properties;
import me.limeglass.skungee.spigot.utils.annotations.PropertiesAddition;

@Name("Bungeecord player reconnect server")
@Description("Returns the reconnected server(s) of the defined Bungeecord player(s). This is the server that the player reconnects to on their next login.")
@Properties({"strings/players", "bungee[[ ]cord] reconnect[ed] server[s]", "{1}[(all [[of] the]|the)]"})
@PropertiesAddition("[(player|uuid)[s]]")
@Changers(ChangeMode.SET)
public class ExprBungeePlayerReconnectServer extends SkungeePropertyExpression<Object, String> {

	@Override
	protected String[] get(Event event, Object[] skungeePlayers) {
		if (isNull(event))
			return null;
		@SuppressWarnings("unchecked")
		Set<String> servers = (Set<String>) Sockets.send(new SkungeePacket(true, SkungeePacketType.PLAYERRECONNECTSERVER, Utils.toSkungeePlayers(skungeePlayers)));
		return (servers != null) ? servers.toArray(new String[servers.size()]) : null;
	}

	@Override
	public void change(Event event, Object[] delta, ChangeMode mode) {
		SkriptChangeMode changer = Utils.getEnum(SkriptChangeMode.class, mode.toString());
		if (isNull(event) || delta == null || changer == null)
			return;
		Sockets.send(new SkungeePacket(false, SkungeePacketType.PLAYERRECONNECTSERVER, (String) delta[0], null, changer, Utils.toSkungeePlayers(getExpr().getAll(event))));
	}

}
