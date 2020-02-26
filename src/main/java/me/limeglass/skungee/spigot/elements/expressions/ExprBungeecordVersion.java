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

@Name("Bungeecord version")
@Description("Returns the version of the Bungeecord.")
@Patterns({"[the] version of [the] bungee[[ ]cord]", "[the] bungee[[ ]cord[[']s]] version"})
@ExpressionProperty(ExpressionType.SIMPLE)
@Single
public class ExprBungeecordVersion extends SkungeeExpression<String> {

	@Override
	protected String[] get(Event event) {
		String version = (String) sockets.send(new ServerPacket(true, ServerPacketType.VERSION));
		return (version != null) ? new String[]{version} : null;
	}

}
