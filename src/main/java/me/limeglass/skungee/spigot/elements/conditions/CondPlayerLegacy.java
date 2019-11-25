package me.limeglass.skungee.spigot.elements.conditions;

import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.skungee.objects.SkungeePlayer;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import me.limeglass.skungee.spigot.lang.SkungeeCondition;
import me.limeglass.skungee.spigot.utils.Utils;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

@Name("Bungeecord player is legacy")
@Description("Check if a player is on a legacy client, usally lower than 1.7. May also be called if the client is not by Mojang.")
@Patterns({"bungee[[ ]cord] [(player|uuid)] %string/player% (1¦is|2¦is(n't| not)) [a] legacy [(client|account)]", "[(player|uuid)] %string/player% (1¦is|2¦is(n't| not)) [a] legacy [(client|account)]"})
public class CondPlayerLegacy extends SkungeeCondition {

	public boolean check(Event event) {
		if (areNull(event))
			return false;
		SkungeePlayer[] player = Utils.toSkungeePlayers(expressions.get(0).getSingle(event));
		SkungeePacket packet = new SkungeePacket(true, SkungeePacketType.PLAYERLEGACY, player);
		return sockets.send(packet, boolean.class) ? isNegated() : !isNegated();
	}

}
