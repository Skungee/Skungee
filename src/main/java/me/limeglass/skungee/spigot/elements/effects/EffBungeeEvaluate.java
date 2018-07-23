package me.limeglass.skungee.spigot.elements.effects;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import me.limeglass.skungee.spigot.lang.SkungeeEffect;
import me.limeglass.skungee.spigot.sockets.Sockets;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

import org.bukkit.event.Event;

@Name("Evaluate")
@Description("Evaluate effects on different servers across the Bungeecord network.")
@Patterns("[skungee] eval[uate] [[skript] code] %strings% on [[the] bungee[[ ]cord]] [server[s]] %strings%")
public class EffBungeeEvaluate extends SkungeeEffect {

	@Override
	protected void execute(Event event) {
		if (areNull(event)) return;
		Sockets.send(new SkungeePacket(false, SkungeePacketType.EVALUATE, expressions.get(0).getArray(event), expressions.get(1).getArray(event)));
	}
}
