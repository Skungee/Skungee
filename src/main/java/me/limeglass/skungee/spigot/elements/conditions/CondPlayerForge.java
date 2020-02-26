package me.limeglass.skungee.spigot.elements.conditions;

import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.common.player.PacketPlayer;
import me.limeglass.skungee.spigot.lang.SkungeeCondition;
import me.limeglass.skungee.spigot.utils.Utils;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

@Name("Bungeecord player forge")
@Description("Check if a player from the Bungeecord is using the Forge client.")
@Patterns("[bungee[[ ]cord]] [(player|uuid)] %string/player% (1¦(has|is (running|using))|2¦(is(n't (running|using)| not))) [the] forge [client]")
public class CondPlayerForge extends SkungeeCondition {

	public boolean check(Event event) {
		if (areNull(event))
			return false;
		PacketPlayer[] players = Utils.toSkungeePlayers(expressions.get(0).getSingle(event));
		ServerPacket packet = new ServerPacket(true, ServerPacketType.ISUSINGFORGE, players);
		return sockets.send(packet, boolean.class) ? isNegated() : !isNegated();
	}

}
