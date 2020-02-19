package me.limeglass.skungee.spigot.elements.effects;

import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.spigot.lang.SkungeeEffect;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

@Name("Evaluate")
@Description("Evaluate effects on different servers across the Bungeecord network.")
@Patterns("[skungee] eval[uate] [[skript] code] %strings% on [[the] bungee[[ ]cord]] [server[s]] %strings%")
public class EffEvaluate extends SkungeeEffect {

	@Override
	protected void execute(Event event) {
		if (areNull(event))
			return;
		ServerPacket packet = new ServerPacket(false, ServerPacketType.EVALUATE, expressions.get(0).getArray(event), expressions.get(1).getArray(event));
		sockets.send(packet);
	}

}
