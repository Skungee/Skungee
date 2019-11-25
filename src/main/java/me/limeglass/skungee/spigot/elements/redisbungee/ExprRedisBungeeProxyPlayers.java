package me.limeglass.skungee.spigot.elements.redisbungee;

import java.util.Set;

import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.ExpressionType;
import me.limeglass.skungee.objects.Returnable;
import me.limeglass.skungee.objects.SkungeePlayer;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import me.limeglass.skungee.spigot.lang.SkungeeExpression;
import me.limeglass.skungee.spigot.utils.annotations.ExpressionProperty;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

@Name("RedisBungee proxy players")
@Description("Returns the players(s) of the defined RedisBungee proxy(s).")
@Patterns({"[(all [[of] the]|the)] redis[( |-)]bungee[[ ]cord] players (on|of|from) [the] prox(ies|y) %strings%", "[(all [[of] the]|the)] players (on|of|from) [the] redis[( |-)]bungee[[ ]cord] prox(ies|y) %strings%"})
@ExpressionProperty(ExpressionType.PROPERTY)
public class ExprRedisBungeeProxyPlayers extends SkungeeExpression<Object> implements Returnable {

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
		Set<SkungeePlayer> players = (Set<SkungeePlayer>) sockets.send(new SkungeePacket(true, SkungeePacketType.REDISPROXYPLAYERS, expressions.getAll(event, String.class)));
		return (players != null) ? convert(players) : null;
	}

}
