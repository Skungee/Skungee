package me.limeglass.skungee.spigot.elements.conditions;

import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.spigot.lang.SkungeeCondition;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

@Name("Bungeecord server online")
@Description("Check if a server is online the Bungeecord.")
@Patterns("[bungee[[ ]cord]] server %string% (1¦is|2¦is(n't| not)) (running|online|responding)")
public class CondServerOnline extends SkungeeCondition {

	public boolean check(Event event) {
		if (areNull(event))
			return false;
		ServerPacket packet = new ServerPacket(true, ServerPacketType.ISSERVERONLINE, expressions.getSingle(event, String.class));
		return sockets.send(packet, boolean.class) ? isNegated() : !isNegated();
	}

}
