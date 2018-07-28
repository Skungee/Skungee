package me.limeglass.skungee.spigot.elements.effects;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.skungee.objects.packets.SkungeePacket;
import me.limeglass.skungee.objects.packets.SkungeePacketType;
import me.limeglass.skungee.spigot.lang.SkungeeEffect;
import me.limeglass.skungee.spigot.sockets.Sockets;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

import org.bukkit.event.Event;

@Name("Bungeecord shutdown server")
@Description("Shutdown a bungeecord server. The saving section of the syntax will allow server instance users to save the files of the server. If not they get deleted.")
@Patterns("(stop|shutdown) bungee[[ ]cord] server[s] %strings% [(and|with) [serverinstances] saving %-boolean%]")
public class EffBungeeShutdownServer extends SkungeeEffect {

	@Override
	protected void execute(Event event) {
		if (isNull(event, String.class)) return;
		if (isNull(event, Boolean.class)) Sockets.send(new SkungeePacket(false, SkungeePacketType.SHUTDOWNSERVER, expressions.getAll(event, String.class)));
		else Sockets.send(new SkungeePacket(false, SkungeePacketType.SHUTDOWNSERVER, expressions.getAll(event, String.class), expressions.getSingle(event, Boolean.class)));
	}
}