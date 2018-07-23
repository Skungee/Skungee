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

@Name("Bungeecord throttle")
@Description("Returns the throttle set in the main configurarion of the Bungeecord.")
@Patterns("[the] bungee[[ ]cord[[']s]] throttle [connection] [delay]")
@ExpressionProperty(ExpressionType.SIMPLE)
@Single
public class ExprBungeeThrottle extends SkungeeExpression<Number> {
	
	@Override
	protected Number[] get(Event event) {
		Number throttle = (Number) Sockets.send(new SkungeePacket(true, SkungeePacketType.BUNGEETHROTTLE));
		return (throttle != null) ? new Number[]{throttle} : null;
	}
}