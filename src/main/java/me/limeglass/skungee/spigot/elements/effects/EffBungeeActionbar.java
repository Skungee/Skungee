package me.limeglass.skungee.spigot.elements.effects;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.skungee.objects.SkungeePacket;
import me.limeglass.skungee.objects.SkungeePacketType;
import me.limeglass.skungee.objects.SkungeePlayer;
import me.limeglass.skungee.spigot.lang.SkungeeEffect;
import me.limeglass.skungee.spigot.sockets.Sockets;
import me.limeglass.skungee.spigot.utils.Utils;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

import org.bukkit.event.Event;

@Name("Actionbar")
@Description("Send a actionbar message to anyone on the Bungeecord network.")
@Patterns("[skungee] (send|display|show) [a[n]] action[ ]bar [with [(text|message)]] %string% to bungee[[ ]cord] [(player|uuid)] %strings/players%")
public class EffBungeeActionbar extends SkungeeEffect {

	@Override
	protected void execute(Event event) {
		if (areNull(event)) return;
		SkungeePlayer[] players = Utils.toSkungeePlayers(expressions.get(1).getAll(event));
		Sockets.send(new SkungeePacket(false, SkungeePacketType.ACTIONBAR, (String) expressions.get(0).getSingle(event), players));
	}
}
