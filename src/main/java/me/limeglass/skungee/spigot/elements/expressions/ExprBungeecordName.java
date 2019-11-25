package me.limeglass.skungee.spigot.elements.expressions;

import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.ExpressionType;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import me.limeglass.skungee.spigot.lang.SkungeeExpression;
import me.limeglass.skungee.spigot.utils.annotations.ExpressionProperty;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;
import me.limeglass.skungee.spigot.utils.annotations.Single;

@Name("Bungeecord name")
@Description("Returns the name of the Bungeecord.")
@Patterns({"[the] name of [the] bungee[[ ]cord]", "[the] bungee[[ ]cord[[']s]] name"})
@ExpressionProperty(ExpressionType.SIMPLE)
@Single
public class ExprBungeecordName extends SkungeeExpression<String> {
	
	@Override
	protected String[] get(Event event) {
		String name = (String) sockets.send(new SkungeePacket(true, SkungeePacketType.BUNGEENAME));
		return (name != null) ? new String[]{name} : null;
	}

}
