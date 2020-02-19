package me.limeglass.skungee.spigot.elements.expressions;

import java.util.Set;

import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.ExpressionType;
import me.limeglass.skungee.common.objects.Returnable;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.common.player.SkungeePlayer;
import me.limeglass.skungee.spigot.lang.SkungeeExpression;
import me.limeglass.skungee.spigot.utils.annotations.ExpressionProperty;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

@Name("Bungeecord server players")
@Description("Returns the players(s) of the defined Bungeecord server(s).")
@Patterns({"[(all [[of] the]|the)] [bungee[[ ]cord]] players (on|of|from) [the] [server[s]] %strings%", "[(all [[of] the]|the)] players (on|of|from) [the] [bungee[[ ]cord]] [server[s]] %strings%"})
@ExpressionProperty(ExpressionType.PROPERTY)
public class ExprBungeeServerPlayers extends SkungeeExpression<Object> implements Returnable {

	@Override
	public Class<? extends Object> getReturnType() {
		return Returnable.getReturnType();
	}

	@Override
	@Nullable
	protected Object[] get(Event event) {
		if (areNull(event) || returnable == null)
			return null;
		@SuppressWarnings("unchecked")
		Set<SkungeePlayer> players = (Set<SkungeePlayer>) sockets.send(new ServerPacket(true, ServerPacketType.SERVERPLAYERS, expressions.getAll(event, String.class)));
		return (players != null) ? convert(players) : null;
	}

}
