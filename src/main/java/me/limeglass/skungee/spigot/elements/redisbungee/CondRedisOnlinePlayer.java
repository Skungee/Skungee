package me.limeglass.skungee.spigot.elements.redisbungee;

import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.skungee.objects.SkungeePacket;
import me.limeglass.skungee.objects.SkungeePacketType;
import me.limeglass.skungee.objects.SkungeePlayer;
import me.limeglass.skungee.spigot.lang.SkungeeCondition;
import me.limeglass.skungee.spigot.sockets.Sockets;
import me.limeglass.skungee.spigot.utils.Utils;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

@Name("RedisBungee player online")
@Description("Check if a player is online the RedisBungeecord. Keep in mind that they need to have joined the proxy and not through a port for it to work.")
@Patterns({"redis[( |-)]bungee[[ ]cord] [(player|uuid)] %string/player% (1¦is|2¦is(n't| not)) online [the] redis[( |-)]bungee[[ ]cord]", "[(player|uuid)] %string/player% (1¦is|2¦is(n't| not)) online [the] redis[( |-)]bungee[[ ]cord]"})
public class CondRedisOnlinePlayer extends SkungeeCondition {

	public boolean check(Event event) {
		if (areNull(event)) return false;
		SkungeePlayer[] players = Utils.toSkungeePlayers(expressions.get(0).getSingle(event));
		return ((Boolean) Sockets.send(new SkungeePacket(true, SkungeePacketType.REDISISPLAYERONLINE, players))) ? isNegated() : !isNegated();
	}
}