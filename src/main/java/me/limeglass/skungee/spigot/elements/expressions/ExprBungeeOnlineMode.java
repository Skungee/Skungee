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

@Name("Bungeecord online mode")
@Description("Returns if the online mode set in the main configurarion of the Bungeecord is true or false.")
@Patterns("[the] bungee[[ ]cord[[']s]] online mode")
@ExpressionProperty(ExpressionType.SIMPLE)
@Single
public class ExprBungeeOnlineMode extends SkungeeExpression<Boolean> {
	
	@Override
	protected Boolean[] get(Event event) {
		Boolean timeout = (Boolean) Sockets.send(new SkungeePacket(true, SkungeePacketType.BUNGEEONLINEMODE));
		return (timeout != null) ? new Boolean[]{timeout} : null;
	}
}