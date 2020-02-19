package me.limeglass.skungee.spigot.elements.effects;

import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.util.Timespan;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.spigot.lang.SkungeeEffect;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

@Name("Proxy console command")
@Description("Execute a console command on the proxy. Commands such as /end, /greload, /alert etc. Can also execute a plugin command if it's supported in console.")
@Patterns({"[skungee] (run|execute) bungee[[ ][cord]] [(proxy|console)] command[s] %strings% [with [a[n]] %-timespan% delay [between [each [command]]]]", "[skungee] make bungee[[ ][cord]] (run|execute) [(proxy|console)] command[s] %strings% [with [a[n]] %-timespan% delay [between [each [command]]]]"})
public class EffExecuteBungeeCommand extends SkungeeEffect {

	//TODO if someone reports this as not working in the future, there is an argument option for the dispatchCommand(), see if they're using a plugin's command and possibly fix it that way, you should know how to fix it from this message.

	@Override
	protected void execute(Event event) {
		long delay = 0;
		if (!isNull(event, Timespan.class)) {
			Timespan timespan = expressions.getSingle(event, Timespan.class);
			if (timespan.getTicks_i() > 0)
				delay = timespan.getMilliSeconds();
		}
		sockets.send(new ServerPacket(false, ServerPacketType.SERVERCOMMAND, expressions.getAll(event, String.class), delay));
	}

}
