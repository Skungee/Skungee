package me.limeglass.skungee.spigot.elements.expressions;

import java.util.Set;

import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.ExpressionType;
import me.limeglass.skungee.objects.Returnable;
import me.limeglass.skungee.objects.SkungeePlayer;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import me.limeglass.skungee.spigot.lang.SkungeeExpression;
import me.limeglass.skungee.spigot.utils.annotations.ExpressionProperty;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

@Name("Bungeecord players")
@Description("Returns a string list of all the bungeecord players.")
@Patterns("[(all [[of] the]|the)] bungee[[ ]cord] players")
@ExpressionProperty(ExpressionType.SIMPLE)
public class ExprBungeePlayers extends SkungeeExpression<Object> implements Returnable {

	@Override
	public Class<? extends Object> getReturnType() {
		return Returnable.getReturnType();
	}

	@Override
	@Nullable
	protected Object[] get(Event event) {
		if (areNull(event) || returnable == null)
			return null;
		@SuppressWarnings("unchecked")
		Set<SkungeePlayer> players = (Set<SkungeePlayer>) sockets.send(new SkungeePacket(true, SkungeePacketType.GLOBALPLAYERS));
		return (players != null) ? convert(players) : null;
	}

}
