package me.limeglass.skungee.spigot.elements.redisbungee;

import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.spigot.lang.SkungeeEffect;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

@Name("RedisBungee Proxy command")
@Description("Execute console command(s) on the defined proxy(ies).")
@Patterns({"(run|execute) redis[( |-)]bungee[[ ][cord]] [(proxy|console)] command[s] %strings% [(on|of|from) [the] [server[s]] %-strings%]", "make redis[( |-)]bungee[[ ][cord]] (run|execute) [(proxy|console)] command[s] %strings% [(on|of|from) [the] [server[s]] %-strings%]"})
public class EffRedisBungeeCommand extends SkungeeEffect {

	@Override
	protected void execute(Event event) {
		if (isNull(event, 0))
			return;
		String[] servers = null;
		if (!isNull(event, 1))
			servers = expressions.getAll(event, String.class, 1);
		sockets.send(new ServerPacket(false, ServerPacketType.REDISPROXYCOMMAND, expressions.get(0).getAll(event), servers));
	}

}
