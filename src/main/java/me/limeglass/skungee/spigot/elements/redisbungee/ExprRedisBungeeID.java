package me.limeglass.skungee.spigot.elements.redisbungee;

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

@Name("RedisBungee server ID")
@Description("Returns the RedisBungee ID of the Bungeecord connected to this Skungee.")
@Patterns("[th(e|is)] [bungee[[ ]cord[[']s]]] redis[( |-)]bungee[[ ]cord] ID")
@ExpressionProperty(ExpressionType.SIMPLE)
@Single
public class ExprRedisBungeeID extends SkungeeExpression<String> {

	@Override
	protected String[] get(Event event) {
		String ID = (String) sockets.send(new ServerPacket(true, ServerPacketType.REDISSERVERID));
		return (ID != null) ? new String[]{ID} : null;
	}

}
