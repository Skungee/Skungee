package me.limeglass.skungee.spigot.elements.expressions;

import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.ExpressionType;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import me.limeglass.skungee.spigot.lang.SkungeeExpression;
import me.limeglass.skungee.spigot.sockets.Sockets;
import me.limeglass.skungee.spigot.utils.annotations.ExpressionProperty;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;
import me.limeglass.skungee.spigot.utils.annotations.Single;

@Name("Bungeecord timeout")
@Description("Returns the timeout set in the main configurarion of the Bungeecord.")
@Patterns("[the] bungee[[ ]cord[[']s]] time[ ]out [connection] [delay]")
@ExpressionProperty(ExpressionType.SIMPLE)
@Single
public class ExprBungeeTimeout extends SkungeeExpression<Number> {
	
	@Override
	protected Number[] get(Event event) {
		Number timeout = (Number) Sockets.send(new SkungeePacket(true, SkungeePacketType.BUNGEETIMEOUT));
		return (timeout != null) ? new Number[]{timeout} : null;
	}
}