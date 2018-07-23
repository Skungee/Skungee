package me.limeglass.skungee.spigot.elements.redisbungee;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import me.limeglass.skungee.spigot.lang.SkungeeEffect;
import me.limeglass.skungee.spigot.sockets.Sockets;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

import org.bukkit.event.Event;

@Name("RedisBungee Proxy command")
@Description("Execute console command(s) on the defined proxy(ies).")
@Patterns({"(run|execute) redis[( |-)]bungee[[ ][cord]] [(proxy|console)] command[s] %strings% [(on|of|from) [the] [server[s]] %-strings%]", "make redis[( |-)]bungee[[ ][cord]] (run|execute) [(proxy|console)] command[s] %strings% [(on|of|from) [the] [server[s]] %-strings%]"})
public class EffRedisBungeeCommand extends SkungeeEffect {

	@Override
	protected void execute(Event event) {
		if (isNull(event, 0)) return;
		String[] servers = null;
		if (!isNull(event, 1)) servers = expressions.getAll(event, String.class, 1);
		Sockets.send(new SkungeePacket(false, SkungeePacketType.REDISPROXYCOMMAND, expressions.get(0).getAll(event), servers));
	}
}
