package me.limeglass.skungee.spigot.elements.effects;

import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.spigot.lang.SkungeeEffect;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

@Name("Bungeecord unregister listeners")
@Description("Unregister the listeners from the defiend plugin(s). This makes it so the plugin doesn't recieve any events. Good if you have a bad plugin.")
@Patterns("unregister [the] listeners from [the] [bungee[[ ]cord]] plugin[s] %strings%")
public class EffUnregisterListeners extends SkungeeEffect {

	@Override
	protected void execute(Event event) {
		if (areNull(event))
			return;
		ServerPacket packet = new ServerPacket(false, ServerPacketType.UNREGISTERLISTENERS, expressions.getAll(event, String.class));
		sockets.send(packet);
	}

}