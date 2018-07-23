package me.limeglass.skungee.spigot.elements.conditions;

import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import me.limeglass.skungee.spigot.lang.SkungeeCondition;
import me.limeglass.skungee.spigot.sockets.Sockets;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

@Name("Bungeecord server online")
@Description("Check if a server is online the Bungeecord.")
@Patterns("[bungee[[ ]cord]] server %string% (1¦is|2¦is(n't| not)) (running|online|responding)")
public class CondServerOnline extends SkungeeCondition {

	public boolean check(Event event) {
		if (areNull(event)) return false;
		Object packet = Sockets.send(new SkungeePacket(true, SkungeePacketType.ISSERVERONLINE, expressions.getSingle(event, String.class)));
		if (packet == null) return !isNegated();
		return ((Boolean)packet) ? isNegated() : !isNegated();
	}
}