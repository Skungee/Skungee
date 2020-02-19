package me.limeglass.skungee.spigot.elements.redisbungee;

import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.common.player.SkungeePlayer;
import me.limeglass.skungee.spigot.lang.SkungeeCondition;
import me.limeglass.skungee.spigot.utils.Utils;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

@Name("RedisBungee player online")
@Description("Check if a player is online the RedisBungeecord. Keep in mind that they need to have joined the proxy and not through a port for it to work.")
@Patterns({"redis[( |-)]bungee[[ ]cord] [(player|uuid)] %string/player% (1¦is|2¦is(n't| not)) online [the] redis[( |-)]bungee[[ ]cord]", "[(player|uuid)] %string/player% (1¦is|2¦is(n't| not)) online [the] redis[( |-)]bungee[[ ]cord]"})
public class CondRedisOnlinePlayer extends SkungeeCondition {

	public boolean check(Event event) {
		if (areNull(event))
			return false;
		SkungeePlayer[] players = Utils.toSkungeePlayers(expressions.get(0).getSingle(event));
		ServerPacket packet = new ServerPacket(true, ServerPacketType.REDISISPLAYERONLINE, players);
		return sockets.send(packet, boolean.class) ? isNegated() : !isNegated();
	}

}
