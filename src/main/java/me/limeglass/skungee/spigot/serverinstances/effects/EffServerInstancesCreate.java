package me.limeglass.skungee.spigot.serverinstances.effects;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.skungee.objects.SkungeePacket;
import me.limeglass.skungee.objects.SkungeePacketType;
import me.limeglass.skungee.spigot.lang.SkungeeEffect;
import me.limeglass.skungee.spigot.sockets.Sockets;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.event.Event;

@Name("ServerInstances - Create server")
@Description("Creates a new server on the Bungeecord proxy based off your templates installed. Tutorial within the expansion.")
@Patterns("(start|create) [a] [new] [bungee[[ ]cord]]] server[s] [named] %strings% (with|from) template %string% [with %-number% xmx [ram] [and] [with] %-number% xms [ram]]")
public class EffServerInstancesCreate extends SkungeeEffect {

	@Override
	protected void execute(Event event) {
		if (isNull(event, String.class)) return;
		List<Object> information = new ArrayList<Object>();
		information.add(expressions.get(1).getSingle(event));
		if (!areNull(event)) information.addAll(Arrays.asList(expressions.getSingle(event, Number.class, 0), expressions.getSingle(event, Number.class, 1)));
		Sockets.send(new SkungeePacket(false, SkungeePacketType.CREATESERVER, expressions.getAll(event, String.class, 0), information));
	}
}