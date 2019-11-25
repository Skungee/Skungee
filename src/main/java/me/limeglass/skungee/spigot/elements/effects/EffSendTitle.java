package me.limeglass.skungee.spigot.elements.effects;

import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.skungee.objects.SkungeePlayer;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import me.limeglass.skungee.spigot.lang.SkungeeEffect;
import me.limeglass.skungee.spigot.utils.Utils;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

@Name("Bungeecord send title")
@Description("Sends a SkugneeTitle to the defined Bungeecord player(s).")
@Patterns("(show|display|send) [skungee] title %skungeetitle% to [bungee[[ ]cord]] [(player|uuid)][s] %strings/players%")
public class EffSendTitle extends SkungeeEffect {

	@Override
	protected void execute(Event event) {
		if (areNull(event))
			return;
		SkungeePlayer[] players = Utils.toSkungeePlayers(expressions.get(1).getAll(event));
		SkungeePacket packet = new SkungeePacket(false, SkungeePacketType.TITLE, expressions.get(0).getSingle(event), null, players);
		sockets.send(packet);
	}

}
