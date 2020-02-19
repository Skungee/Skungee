package me.limeglass.skungee.spigot.elements.effects;

import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.spigot.lang.SkungeeEffect;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

@Name("Bungeecord unregister commands")
@Description("Unregister the commands from the defiend plugin(s).")
@Patterns("unregister [the] commands from [the] [bungee[[ ]cord]] plugin[s] %strings%")
public class EffUnregisterCommands extends SkungeeEffect {

	@Override
	protected void execute(Event event) {
		if (areNull(event))
			return;
		ServerPacket packet = new ServerPacket(false, ServerPacketType.UNREGISTERCOMMANDS, expressions.getAll(event, String.class));
		sockets.send(packet);
	}

}