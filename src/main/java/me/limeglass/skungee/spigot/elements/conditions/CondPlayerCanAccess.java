package me.limeglass.skungee.spigot.elements.conditions;

import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.skungee.objects.SkungeePlayer;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import me.limeglass.skungee.spigot.lang.SkungeeCondition;
import me.limeglass.skungee.spigot.sockets.Sockets;
import me.limeglass.skungee.spigot.utils.Utils;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

@Name("Bungeecord player can access")
@Description("Check if a player has the right to join a server.")
@Patterns("[bungee[[ ]cord]] [(player|uuid)] %string/player% (1¦can|2¦can(n't| not)) (connect|join|login|log on) [to] [the] [server] %string%")
public class CondPlayerCanAccess extends SkungeeCondition {

	public boolean check(Event event) {
		if (areNull(event)) return false;
		SkungeePlayer[] players = Utils.toSkungeePlayers(expressions.get(0).getSingle(event));
		return ((Boolean) Sockets.send(new SkungeePacket(true, SkungeePacketType.PLAYERACCESS, expressions.get(1).getSingle(event), null, players))) ? isNegated() : !isNegated();
	}
}