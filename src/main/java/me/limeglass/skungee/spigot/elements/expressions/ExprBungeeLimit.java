package me.limeglass.skungee.spigot.elements.expressions;

import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.ExpressionType;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.spigot.lang.SkungeeExpression;
import me.limeglass.skungee.spigot.utils.annotations.ExpressionProperty;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;
import me.limeglass.skungee.spigot.utils.annotations.Single;

@Name("Bungeecord limit")
@Description("Returns the max number of players the Bungeecord is set to handle.")
@Patterns("[the] bungee[[ ]cord[[']s]] player limit")
@ExpressionProperty(ExpressionType.SIMPLE)
@Single
public class ExprBungeeLimit extends SkungeeExpression<Number> {

	@Override
	protected Number[] get(Event event) {
		Number limit = (Number) sockets.send(new ServerPacket(true, ServerPacketType.BUNGEEPLAYERLIMIT));
		return (limit != null) ? new Number[]{limit} : null;
	}

}
