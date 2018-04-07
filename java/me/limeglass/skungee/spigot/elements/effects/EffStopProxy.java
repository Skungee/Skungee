package me.limeglass.skungee.spigot.elements.effects;

import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.skungee.objects.SkungeePacket;
import me.limeglass.skungee.objects.SkungeePacketType;
import me.limeglass.skungee.spigot.lang.SkungeeEffect;
import me.limeglass.skungee.spigot.sockets.Sockets;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

@Name("Stop proxy")
@Description("Ends the Bungeecord proxy. You can use \\n to make a new line in the message string.")
@Patterns("[skungee] (stop|kill|end) [the] [bungee[[ ]cord]] (proxy|console) [[with] [the] (message|string|text) %-string%]")
public class EffStopProxy extends SkungeeEffect {

	@Override
	protected void execute(Event event) {
		Sockets.send(new SkungeePacket(false, SkungeePacketType.PROXYSTOP, expressions.getSingle(event, String.class)));
	}
}