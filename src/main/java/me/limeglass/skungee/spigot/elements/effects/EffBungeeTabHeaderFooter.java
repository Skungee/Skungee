package me.limeglass.skungee.spigot.elements.effects;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.skungee.objects.SkungeePlayer;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import me.limeglass.skungee.spigot.lang.SkungeeEffect;
import me.limeglass.skungee.spigot.sockets.Sockets;
import me.limeglass.skungee.spigot.utils.Utils;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

import org.bukkit.event.Event;

@Name("Bungeecord tablist header and footer")
@Description("Set the tablist header and/or footer of a player on the Bungeecord network. This may reset when they switch servers.")
@Patterns("set tab[list] (1¦header|2¦footer|3¦header and footer) (for|of) bungee[[ ]cord] [(player|uuid)[s]] %strings/players% to %strings% [and %-strings%]")
public class EffBungeeTabHeaderFooter extends SkungeeEffect {

	@Override
	protected void execute(Event event) {
		if (isNull(event, 0) || isNull(event, 1)) return;
		SkungeePlayer[] players = Utils.toSkungeePlayers(expressions.get(0).getAll(event));
		if (isNull(event, 2)) {
			Sockets.send(new SkungeePacket(false, SkungeePacketType.TABHEADERFOOTER, (String[]) expressions.get(1).getArray(event), patternMark, players));
		} else Sockets.send(new SkungeePacket(false, SkungeePacketType.TABHEADERFOOTER, (String[]) expressions.get(1).getArray(event), (String[]) expressions.get(2).getArray(event), players));
	}
}
