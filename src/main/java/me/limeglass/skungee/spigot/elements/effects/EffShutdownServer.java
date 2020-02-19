package me.limeglass.skungee.spigot.elements.effects;

import org.bukkit.event.Event;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import me.limeglass.skungee.common.packets.ServerPacket;
import me.limeglass.skungee.common.packets.ServerPacketType;
import me.limeglass.skungee.spigot.lang.SkungeeEffect;
import me.limeglass.skungee.spigot.utils.annotations.Patterns;

@Name("Bungeecord shutdown server")
@Description("Shutdown a bungeecord server. The saving section of the syntax will allow server instance users to save the files of the server. If not they get deleted.")
@Patterns("(stop|shutdown) bungee[[ ]cord] server[s] %strings% [(and|with) [serverinstances] saving %-boolean%]")
public class EffShutdownServer extends SkungeeEffect {

	@Override
	protected void execute(Event event) {
		if (isNull(event, String.class))
			return;
		if (isNull(event, boolean.class)) {
			ServerPacket packet = new ServerPacket(false, ServerPacketType.SHUTDOWNSERVER, expressions.getAll(event, String.class));
			sockets.send(packet);
		} else {
			ServerPacket packet = new ServerPacket(false, ServerPacketType.SHUTDOWNSERVER, expressions.getAll(event, String.class), expressions.getSingle(event, boolean.class));
			sockets.send(packet);
		}
	}

}
