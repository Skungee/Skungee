package me.limeglass.skungee.spigot.elements.conditions;

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

@Name("Bungeecord player colours")
@Description("Check if a player has chat colors enabled or disabled.")
@Patterns("[bungee[[ ]cord]] [(player|uuid)] %string/player% (1¦(has|do[es])|2¦(has|do[es])(n't| not)) (have|got) chat colo[u]r[s] [(enabled|on)]")
public class CondPlayerHasColours extends SkungeeCondition {

	public boolean check(Event event) {
		if (areNull(event)) return false;
		SkungeePlayer[] players = Utils.toSkungeePlayers(expressions.get(0).getSingle(event));
		return ((Boolean) Sockets.send(new SkungeePacket(true, SkungeePacketType.PLAYERCOLOURS, players))) ? isNegated() : !isNegated();
	}
}