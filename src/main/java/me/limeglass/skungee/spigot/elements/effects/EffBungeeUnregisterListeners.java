package me.limeglass.skungee.spigot.elements.effects;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.skungee.objects.SkungeePacket;
import me.limeglass.skungee.objects.SkungeePacketType;
import me.limeglass.skungee.spigot.lang.SkungeeEffect;
import me.limeglass.skungee.spigot.sockets.Sockets;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

import org.bukkit.event.Event;

@Name("Bungeecord unregister listeners")
@Description("Unregister the listeners from the defiend plugin(s). This makes it so the plugin doesn't recieve any events. Good if you have a bad plugin.")
@Patterns("unregister [the] listeners from [the] [bungee[[ ]cord]] plugin[s] %strings%")
public class EffBungeeUnregisterListeners extends SkungeeEffect {

	@Override
	protected void execute(Event event) {
		if (areNull(event)) return;
		Sockets.send(new SkungeePacket(false, SkungeePacketType.UNREGISTERLISTENERS, expressions.getAll(event, String.class)));
	}
}