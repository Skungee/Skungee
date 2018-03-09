package me.limeglass.skungee.spigot.elements.conditions;

import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.skungee.objects.SkungeePacket;
import me.limeglass.skungee.objects.SkungeePacketType;
import me.limeglass.skungee.spigot.lang.SkungeeCondition;
import me.limeglass.skungee.spigot.sockets.Sockets;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

@Name("Bungeecord player online")
@Description("Check if a player is online the Bungeecord. Keep in mind that they need to have joined the proxy and not through a port for it to work.")
@Patterns("[bungee[[ ]cord]] server %string% (1¦is|2¦is(n't| not)) (running|online)")
public class CondServerOnline extends SkungeeCondition {

	public boolean check(Event event) {
		if (areNull(event)) return false;
		return ((Boolean) Sockets.send(new SkungeePacket(true, SkungeePacketType.ISSERVERONLINE, expressions.getSingle(event, String.class)))) ? isNegated() : !isNegated();
	}
}